package kz.temirlan.studentapi.info;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
public class ApplicationInfoController {

    private final BuildProperties buildProperties;

    public ApplicationInfoController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping
    public ApplicationInfo getApplicationInfo() {
        return new ApplicationInfo("Temirlan Alzhan", "IT2-2307", "38372", buildProperties.getVersion());
    }
}
