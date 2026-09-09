package dev.kotoba.api;
import dev.kotoba.api.Models.*;
import dev.kotoba.service.LearningService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.*;
@RestController @RequestMapping("/api/v1") public class LearningController {
 private final LearningService service; public LearningController(LearningService s){service=s;}
 private UUID user(Jwt jwt){return UUID.fromString(jwt.getSubject());}
 private String type(String collection){return switch(collection){case "vocabulary"->"VOCABULARY";case "grammar"->"GRAMMAR";case "kanji"->"KANJI";case "personal-vocabulary"->"PERSONAL";default->throw new ResponseStatusException(HttpStatus.NOT_FOUND);};}
 @GetMapping("/{collection:vocabulary|grammar|kanji|personal-vocabulary}") public PageView<ItemView> list(@AuthenticationPrincipal Jwt jwt,@PathVariable String collection,@RequestParam(required=false) String level,@RequestParam(defaultValue="") String search,@RequestParam(required=false) String status,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="30") int size,@RequestParam(defaultValue="newest") String sort,@RequestParam(defaultValue="false") boolean favorites){return service.list(user(jwt),type(collection),level,search,status,page,size,sort,favorites);}
 @GetMapping("/{collection:vocabulary|grammar|kanji|personal-vocabulary}/{id}") public ItemView get(@AuthenticationPrincipal Jwt jwt,@PathVariable String collection,@PathVariable UUID id){var i=service.get(user(jwt),id);if(!i.type().equals(type(collection)))throw new ResponseStatusException(HttpStatus.NOT_FOUND);return i;}
 @PostMapping("/personal-vocabulary") public ResponseEntity<ItemView> create(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody PersonalInput input){var item=service.create(user(jwt),input);return ResponseEntity.created(java.net.URI.create("/api/v1/personal-vocabulary/"+item.id())).body(item);}
 @PutMapping("/personal-vocabulary/{id}") public ItemView update(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID id,@Valid @RequestBody PersonalInput input){return service.update(user(jwt),id,input);}
 @DeleteMapping("/personal-vocabulary/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID id){service.delete(user(jwt),id);}
 @PutMapping("/items/{id}/state") public ItemView state(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID id,@Valid @RequestBody StateInput input){return service.setState(user(jwt),id,input);}
 @GetMapping("/reviews/today") public List<ItemView> due(@AuthenticationPrincipal Jwt jwt){return service.due(user(jwt));}
 @PostMapping("/reviews/{itemType}/{id}") public ItemView grade(@AuthenticationPrincipal Jwt jwt,@PathVariable String itemType,@PathVariable UUID id,@Valid @RequestBody GradeInput input){return service.grade(user(jwt),itemType,id,input);}
 @GetMapping("/progress") public Stats stats(@AuthenticationPrincipal Jwt jwt){return service.stats(user(jwt));}
 @GetMapping("/settings") public SettingsView settings(@AuthenticationPrincipal Jwt jwt){return service.settings(user(jwt));}
 @PutMapping("/settings") public SettingsView settings(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody SettingsInput s){return service.settings(user(jwt),s);}
}
