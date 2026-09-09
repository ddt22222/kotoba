package dev.kotoba;
import dev.kotoba.domain.*;import dev.kotoba.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.util.*;import java.time.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") class LearningApiTest {
 @Autowired MockMvc mvc;@Autowired ItemRepository items;@Autowired ProgressRepository progress;@Autowired ReviewRepository reviews;@Autowired ProfileRepository profiles;
 UUID a,b; Item vocab;
 @BeforeEach void setup(){reviews.deleteAll();progress.deleteAll();items.deleteAll();profiles.deleteAll();a=UUID.randomUUID();b=UUID.randomUUID();var i=new Item();i.type="VOCABULARY";i.word="食べる";i.reading="たべる";i.meaningVi="ăn";i.jlptLevel="N5";vocab=items.save(i);}
 org.springframework.test.web.servlet.request.RequestPostProcessor auth(UUID id){return jwt().jwt(j->j.subject(id.toString()).claim("role","authenticated").audience(List.of("authenticated")));}
 String body(String word,long version){return "{\"word\":\""+word+"\",\"meaningVi\":\"nghĩa\",\"exampleSentence\":\"例文\",\"version\":"+version+"}";}
 @Test void requiresAuthentication() throws Exception {mvc.perform(get("/api/v1/personal-vocabulary")).andExpect(status().isUnauthorized());mvc.perform(get("/health")).andExpect(status().isOk());}
 @Test void crudIsPrivateAndConflictsAreDetected() throws Exception {
  mvc.perform(post("/api/v1/personal-vocabulary").with(auth(a)).contentType(MediaType.APPLICATION_JSON).content(body("見積もり",0))).andExpect(status().isCreated()).andExpect(jsonPath("$.word").value("見積もり"));
  var id=items.findAll().stream().filter(i->a.equals(i.ownerId)).findFirst().orElseThrow().id;
  mvc.perform(get("/api/v1/personal-vocabulary").with(auth(b))).andExpect(jsonPath("$.totalElements").value(0));
  for(var method:List.of(get("/api/v1/personal-vocabulary/"+id),delete("/api/v1/personal-vocabulary/"+id),put("/api/v1/personal-vocabulary/"+id).contentType(MediaType.APPLICATION_JSON).content(body("stolen",0))))mvc.perform(method.with(auth(b))).andExpect(status().isNotFound());
  mvc.perform(put("/api/v1/items/"+id+"/state").with(auth(b)).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"MASTERED\",\"favorite\":true}")).andExpect(status().isNotFound());
  mvc.perform(post("/api/v1/reviews/PERSONAL/"+id).with(auth(b)).contentType(MediaType.APPLICATION_JSON).content("{\"grade\":\"GOOD\",\"requestId\":\""+UUID.randomUUID()+"\"}")).andExpect(status().isNotFound());
  mvc.perform(put("/api/v1/personal-vocabulary/"+id).with(auth(a)).contentType(MediaType.APPLICATION_JSON).content(body("変更",0))).andExpect(status().isOk()).andExpect(jsonPath("$.version").value(1));
  mvc.perform(put("/api/v1/personal-vocabulary/"+id).with(auth(a)).contentType(MediaType.APPLICATION_JSON).content(body("old",0))).andExpect(status().isConflict());
  mvc.perform(delete("/api/v1/personal-vocabulary/"+id).with(auth(a))).andExpect(status().isNoContent());assertThat(items.findById(id)).isEmpty();
 }
 @Test void rejectsInvalidFieldsAndFilters() throws Exception {
  mvc.perform(post("/api/v1/personal-vocabulary").with(auth(a)).contentType(MediaType.APPLICATION_JSON).content(body(" ",0))).andExpect(status().isBadRequest());
  mvc.perform(get("/api/v1/vocabulary?size=101").with(auth(a))).andExpect(status().isBadRequest());
  mvc.perform(get("/api/v1/vocabulary?level=N9").with(auth(a))).andExpect(status().isBadRequest());
  mvc.perform(get("/api/v1/vocabulary?search=たべ&level=N5&size=1").with(auth(a))).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
  mvc.perform(get("/api/v1/vocabulary?search=ăn&level=N4").with(auth(a))).andExpect(jsonPath("$.totalElements").value(0));
  mvc.perform(get("/api/v1/vocabulary?search=%25").with(auth(a))).andExpect(jsonPath("$.totalElements").value(0));
 }
 @Test void reviewIsIdempotentAndProgressIsPerUser() throws Exception {
  String payload="{\"grade\":\"GOOD\",\"requestId\":\""+UUID.randomUUID()+"\"}";
  for(int n=0;n<2;n++)mvc.perform(post("/api/v1/reviews/VOCABULARY/"+vocab.id).with(auth(a)).contentType(MediaType.APPLICATION_JSON).content(payload)).andExpect(status().isOk());
  assertThat(reviews.count()).isEqualTo(1);assertThat(progress.findByUserIdAndItemId(a,vocab.id).orElseThrow().reviewCount).isEqualTo(1);
  mvc.perform(get("/api/v1/progress").with(auth(a))).andExpect(jsonPath("$.totalReviews").value(1)).andExpect(jsonPath("$.accuracy").value(100)).andExpect(jsonPath("$.streak").value(1));
  mvc.perform(get("/api/v1/progress").with(auth(b))).andExpect(jsonPath("$.totalReviews").value(0));
  mvc.perform(get("/api/v1/vocabulary?status=NEW").with(auth(a))).andExpect(jsonPath("$.totalElements").value(0));
  mvc.perform(get("/api/v1/vocabulary?status=NEW").with(auth(b))).andExpect(jsonPath("$.totalElements").value(1));
  mvc.perform(get("/api/v1/reviews/today").with(auth(a))).andExpect(jsonPath("$.length()").value(0));
 }
 @Test void settingsValidateTimezoneAndPersist() throws Exception {
  mvc.perform(put("/api/v1/settings").with(auth(a)).contentType(MediaType.APPLICATION_JSON).content("{\"timezone\":\"Mars/City\",\"dailyGoal\":10,\"theme\":\"dark\",\"targetLevel\":\"N1\"}")).andExpect(status().isBadRequest());
  mvc.perform(put("/api/v1/settings").with(auth(a)).contentType(MediaType.APPLICATION_JSON).content("{\"timezone\":\"Asia/Ho_Chi_Minh\",\"dailyGoal\":20,\"theme\":\"dark\",\"targetLevel\":\"N1\"}")).andExpect(status().isOk());
  mvc.perform(get("/api/v1/settings").with(auth(a))).andExpect(jsonPath("$.dailyGoal").value(20)).andExpect(jsonPath("$.theme").value("dark"));
 }
}
