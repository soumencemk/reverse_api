package com.soumen.webhookdemo;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Soumen Karmakar
 * 10-Oct-19
 */
@RestController
@RequestMapping("/webhooks")
@Log4j2
public class WebHookController {
    @Autowired
    private WebHookRepository webHookRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WebHook>> getAllWebHooks() {
        List<WebHook> webhooks = new ArrayList<>();
        webHookRepository.findAll().iterator().forEachRemaining(webhooks::add);
        return new ResponseEntity<>(webhooks, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_MARKDOWN_VALUE)
    public ResponseEntity<WebHook> addWebHook(@RequestBody WebHook webHook) {
        log.info("New webhook for " + webHook.getCompanyName() + " is registered");
        List<WebHook> webhooks = webHookRepository.findByCompanyNameAndType(
                webHook.getCompanyName(),
                webHook.getType());
        if (webhooks != null && webhooks.contains(webHook)) {
            return new ResponseEntity<>(webHook, HttpStatus.CONFLICT);
        }
        WebHook save = webHookRepository.save(webHook);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @GetMapping(
            value = "/comapnies/{companyName}/types/{type}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebHook> getWebHooksByCompanyNameAndType(
            @PathVariable String companyName,
            @PathVariable String type) {
        List<WebHook> webhooks = webHookRepository.findByCompanyNameAndType(
                companyName, type);
        return new ResponseEntity<WebHook>(webhooks.get(0), HttpStatus.OK);
    }

    @DeleteMapping(
            value = "/comapnies/{companyName}/types/{type}",
            produces = MediaType.TEXT_MARKDOWN_VALUE)
    public ResponseEntity<String> removeWebHook(
            @PathVariable String companyName,
            @PathVariable String type) {
        List<WebHook> webhooks = webHookRepository.findByCompanyNameAndType(
                companyName, type);
        if (!webhooks.isEmpty()) {
            webHookRepository.delete(webhooks.get(0));
            return new ResponseEntity<>("WebHook was successfully deleted.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Webhook doesn't exist.", HttpStatus.OK);
    }

    @DeleteMapping(value = "/ids/{id}",
            produces = MediaType.TEXT_MARKDOWN_VALUE)
    public ResponseEntity<String> removeWebHookById(@PathVariable Long id) {
        WebHook webhook = webHookRepository.findById(id).get();
        if (webhook != null) {
            webHookRepository.delete(webhook);
            return new ResponseEntity<>("WebHook was successfully deleted.", HttpStatus.OK);
        }
        return new ResponseEntity<>("WebHook was successfully deleted.", HttpStatus.OK);
    }

}

@Repository
interface WebHookRepository extends CrudRepository<WebHook, Long> {
    public List<WebHook> findByCompanyNameAndType(String companyName, String type);

    public List<WebHook> findByCompanyName(String companyName);
}
