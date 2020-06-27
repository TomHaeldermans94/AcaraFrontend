package be.acara.frontend.validators;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldsValueMatchValidator implements ConstraintValidator<FieldsValueMatch, Object> {
    private String field;
    private String fieldMatch;
    
    @Override
    public void initialize(FieldsValueMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }
    
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext ctx) {
        Object fieldValue = new BeanWrapperImpl(object).getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(object).getPropertyValue(fieldMatch);
        
        if (fieldValue != null && !fieldValue.equals(fieldMatchValue)) {
            addConstraintViolation(ctx, field);
            addConstraintViolation(ctx, fieldMatch);
            return false;
        }
        return true;
        
    }
    
    private void addConstraintViolation(ConstraintValidatorContext ctx, String node) {
        ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
                .addPropertyNode(node)
                .addConstraintViolation();
    }
}
