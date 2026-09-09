package dev.kotoba.service;
import dev.kotoba.api.Models.*;
import dev.kotoba.domain.*;
import dev.kotoba.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.*;
import jakarta.persistence.criteria.*;
import java.time.*;
import java.util.*;
@Service @Transactional public class LearningService {
 private final ItemRepository items; private final ProgressRepository progress;private final ReviewRepository reviews;private final ProfileRepository profiles;private final ReviewScheduler scheduler;private final Clock clock;
 public LearningService(ItemRepository i,ProgressRepository p,ReviewRepository r,ProfileRepository f,ReviewScheduler s,Clock c){items=i;progress=p;reviews=r;profiles=f;scheduler=s;clock=c;}
 private Profile profile(UUID user){return profiles.findById(user).orElseGet(()->{var p=new Profile();p.id=user;return profiles.saveAndFlush(p);});}
 private void lock(UUID user){profile(user);profiles.lock(user);}
 private Item accessible(UUID user,UUID id){var i=items.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(i.ownerId!=null&&!i.ownerId.equals(user))throw new ResponseStatusException(HttpStatus.NOT_FOUND);return i;}
 private Progress state(UUID user,Item item){return progress.findByUserIdAndItemId(user,item.id).orElseGet(()->{var p=new Progress();p.userId=user;p.item=item;return p;});}
 private ItemView view(UUID user,Item i){return ItemView.of(i,progress.findByUserIdAndItemId(user,i.id).orElse(null));}
 public PageView<ItemView> list(UUID user,String type,String level,String search,String status,int page,int size,String sort,boolean favorites){
  if(page<0||size<1||size>100||search.length()>200||!Set.of("VOCABULARY","GRAMMAR","KANJI","PERSONAL").contains(type)||level!=null&&!level.matches("N[1-5]")||status!=null&&!Set.of("NEW","LEARNING","REVIEW","MASTERED").contains(status)||!Set.of("newest","oldest","word").contains(sort))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Bộ lọc không hợp lệ.");
  var ordering=sort.equals("word")?Sort.by("word").ascending():sort.equals("oldest")?Sort.by("createdAt").ascending():Sort.by("createdAt").descending();
  var result=items.findAll((root,q,cb)->{
   var ps=new ArrayList<Predicate>();ps.add(cb.equal(root.get("type"),type));ps.add(type.equals("PERSONAL")?cb.equal(root.get("ownerId"),user):cb.isNull(root.get("ownerId")));
   if(level!=null)ps.add(cb.equal(root.get("jlptLevel"),level));
   if(!search.isBlank()){String s="%"+search.toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";ps.add(cb.or(cb.like(cb.lower(root.get("word")),s,'\\'),cb.like(cb.lower(root.get("reading")),s,'\\'),cb.like(cb.lower(root.get("meaningVi")),s,'\\'),cb.like(cb.lower(root.get("onyomi")),s,'\\'),cb.like(cb.lower(root.get("kunyomi")),s,'\\')));}
   if(status!=null){var sub=q.subquery(UUID.class);var p=sub.from(Progress.class);var statePredicate="NEW".equals(status)?cb.notEqual(p.get("status"),"NEW"):cb.equal(p.get("status"),status);sub.select(p.get("id")).where(cb.equal(p.get("userId"),user),cb.equal(p.get("item").get("id"),root.get("id")),statePredicate);ps.add("NEW".equals(status)?cb.not(cb.exists(sub)):cb.exists(sub));}
   if(favorites){var sub=q.subquery(UUID.class);var p=sub.from(Progress.class);sub.select(p.get("id")).where(cb.equal(p.get("userId"),user),cb.equal(p.get("item").get("id"),root.get("id")),cb.isTrue(p.get("favorite")));ps.add(cb.exists(sub));}
   return cb.and(ps.toArray(Predicate[]::new));
  },PageRequest.of(page,size,ordering.and(Sort.by("id"))));
  var states=new HashMap<UUID,Progress>();progress.findByUserIdAndItemIdIn(user,result.getContent().stream().map(i->i.id).toList()).forEach(p->states.put(p.item.id,p));
  return new PageView<>(result.getContent().stream().map(i->ItemView.of(i,states.get(i.id))).toList(),result.getTotalElements(),result.getTotalPages(),page,size);
 }
 public ItemView get(UUID user,UUID id){return view(user,accessible(user,id));}
 public ItemView create(UUID user,PersonalInput input){profile(user);var i=new Item();i.ownerId=user;i.type="PERSONAL";fill(i,input);return ItemView.of(items.save(i),null);}
 private void fill(Item i,PersonalInput p){i.word=p.word().strip();i.meaningVi=p.meaningVi().strip();i.exampleSentence=Objects.requireNonNullElse(p.exampleSentence(),"");i.reading=Objects.requireNonNullElse(p.reading(),"");i.jlptLevel=p.jlptLevel();i.notes=Objects.requireNonNullElse(p.notes(),"");i.updatedAt=clock.instant();}
 public ItemView update(UUID user,UUID id,PersonalInput input){lock(user);var i=accessible(user,id);if(!user.equals(i.ownerId))throw new ResponseStatusException(HttpStatus.NOT_FOUND);if(i.version!=input.version())throw new ResponseStatusException(HttpStatus.CONFLICT,"Từ đã thay đổi ở nơi khác. Hãy tải lại.");fill(i,input);items.saveAndFlush(i);return view(user,i);}
 public void delete(UUID user,UUID id){lock(user);var i=accessible(user,id);if(!user.equals(i.ownerId))throw new ResponseStatusException(HttpStatus.NOT_FOUND);reviews.deleteByItemId(id);progress.deleteByItemId(id);items.delete(i);}
 public ItemView setState(UUID user,UUID id,StateInput input){lock(user);var i=accessible(user,id);var p=state(user,i);p.status=input.status().name();p.favorite=input.favorite();p.nextReviewAt=input.status()==State.NEW?null:clock.instant();progress.save(p);return ItemView.of(i,p);}
 public ItemView grade(UUID user,String type,UUID id,GradeInput input){lock(user);var i=accessible(user,id);if(!i.type.equals(type))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Loại nội dung không đúng.");var p=state(user,i);if(reviews.existsByUserIdAndRequestId(user,input.requestId()))return ItemView.of(i,p);scheduler.apply(p,input.grade(),clock.instant());progress.save(p);var event=new ReviewEvent();event.item=i;event.userId=user;event.requestId=input.requestId();event.grade=input.grade().name();event.reviewedAt=clock.instant();reviews.save(event);return ItemView.of(i,p);}
 public List<ItemView> due(UUID user){return progress.findByUserId(user).stream().filter(p->p.nextReviewAt!=null&&!p.nextReviewAt.isAfter(clock.instant())).sorted(Comparator.comparing(p->p.nextReviewAt)).limit(100).map(p->ItemView.of(p.item,p)).toList();}
 public SettingsView settings(UUID user){var p=profile(user);return new SettingsView(p.timezone,p.dailyGoal,p.theme,p.targetLevel);}
 public SettingsView settings(UUID user,SettingsInput s){try{ZoneId.of(s.timezone());}catch(DateTimeException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Múi giờ không hợp lệ.");}var p=profile(user);p.timezone=s.timezone();p.dailyGoal=s.dailyGoal();p.theme=s.theme();p.targetLevel=s.targetLevel();return settings(user);}
 public Stats stats(UUID user){
  var profile=profile(user);var zone=ZoneId.of(profile.timezone);var today=LocalDate.now(clock.withZone(zone));var states=progress.findByUserId(user);var events=reviews.findByUserIdAndReviewedAtGreaterThanEqualOrderByReviewedAtDesc(user,Instant.EPOCH);
  var days=new HashMap<LocalDate,List<ReviewEvent>>();events.forEach(e->days.computeIfAbsent(e.reviewedAt.atZone(zone).toLocalDate(),k->new ArrayList<>()).add(e));
  int streak=0;var day=days.containsKey(today)?today:today.minusDays(1);while(days.containsKey(day)){streak++;day=day.minusDays(1);}
  var levels=new ArrayList<LevelStats>();for(var level:List.of("N5","N4","N3","N2","N1"))for(var type:List.of("VOCABULARY","GRAMMAR","KANJI")){
   long total=items.count((root,q,cb)->cb.and(cb.isNull(root.get("ownerId")),cb.equal(root.get("type"),type),cb.equal(root.get("jlptLevel"),level)));
   long learned=states.stream().filter(p->p.item.type.equals(type)&&level.equals(p.item.jlptLevel)&&!p.status.equals("NEW")).count();levels.add(new LevelStats(level,type,total,learned));}
  var chart=new ArrayList<DayStats>();for(int n=29;n>=0;n--){var d=today.minusDays(n);var es=days.getOrDefault(d,List.of());chart.add(new DayStats(d.toString(),es.size(),es.stream().filter(e->!e.grade.equals("AGAIN")).count()));}
  long correct=events.stream().filter(e->!e.grade.equals("AGAIN")).count();var recent=events.stream().map(e->e.item.id).distinct().limit(5).map(id->get(user,id)).toList();
  return new Stats(learned(states,"VOCABULARY"),learned(states,"GRAMMAR"),learned(states,"KANJI"),learned(states,"PERSONAL"),events.size(),events.isEmpty()?0:100.0*correct/events.size(),streak,days.getOrDefault(today,List.of()).size(),states.stream().filter(p->p.nextReviewAt!=null&&!p.nextReviewAt.isAfter(clock.instant())).count(),profile.dailyGoal,levels,chart,recent);
 }
 private long learned(List<Progress> states,String type){return states.stream().filter(p->p.item.type.equals(type)&&!p.status.equals("NEW")).count();}
}
