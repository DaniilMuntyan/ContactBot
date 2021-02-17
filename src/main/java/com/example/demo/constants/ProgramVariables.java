package com.example.demo.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@PropertySources({
        @PropertySource("classpath:program.properties"),
        @PropertySource("classpath:callback.properties")
})
public final class ProgramVariables {
    @Value("${unknown_numbers.file.path}")
    @Getter
    private String unknownNumbersFilePath;

    @Value("${unknown_numbers.message.text}")
    @Getter
    private String unknownNumbersMsgText;

    @Value("${unknown_numbers.file.fail}")
    @Getter
    private String unknownNumbersFail;

    @Value("${backup.file.path}")
    @Getter
    private String backupFilePath;

    @Value("${backup.file.caption}")
    @Getter
    private String backupCaption;

    @Value("${backup.fail}")
    @Getter
    private String backupFail;

    @Value("${dates.format}")
    @Getter
    private String dateFormat;

    @Value("${button.delete.callback.yes}")
    @Getter
    private String deleteCallbackYes;

    @Value("${button.delete.callback.no}")
    @Getter
    private String deleteCallbackNo;
}
