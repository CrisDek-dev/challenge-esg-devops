package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "hook"},
        tags = "@regressivo",
        plugin = {
                "pretty",
                "summary",
                "html:target/cucumber-reports.html",
                "json:target/cucumber.json",
                "junit:target/cucumber-junit.xml"
        },
        monochrome = true
)
public class TestRunner {
}
