package xyz.krsh.insecuresite.security.hibernateValidatorBootstrapping;

import java.util.Date;
import java.util.Locale;

import javax.validation.MessageInterpolator;

public class MyMessageInterpolator implements MessageInterpolator {

    @Override
    public String interpolate(String messageTemplate, Context context) {
        System.out.println("Message template: " + messageTemplate);
        StringBuilder stringBuilder = new StringBuilder(
                "==================\n This is my custom message interpolator\n ");
        stringBuilder.append(new Date() + "\n");
        stringBuilder.append(" Validated Value: " + context.getValidatedValue().toString());
        stringBuilder.append("\n Violated constraint " + context.getConstraintDescriptor().getAnnotation());

        stringBuilder.append("\n==================");
        messageTemplate = stringBuilder.toString();
        return messageTemplate;

    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        System.out.println("Message template: " + messageTemplate);
        StringBuilder stringBuilder = new StringBuilder(
                "===================\n This is my custom message interpolator\n ");
        stringBuilder.append(" Validated Value: " + context.getValidatedValue().toString());
        stringBuilder.append("\n Violated constraint" + context.getConstraintDescriptor());

        stringBuilder.append("\n==================");
        messageTemplate = stringBuilder.toString();
        return messageTemplate;
    }

}