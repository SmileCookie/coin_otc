package com.google.code.morphia.mapping.validation.fieldrules;


import com.google.code.morphia.mapping.MappedClass;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.validation.ClassConstraint;
import com.google.code.morphia.mapping.validation.ConstraintViolation;

import java.util.Set;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public abstract class FieldConstraint implements ClassConstraint {
    public final void check(final MappedClass mc, final Set<ConstraintViolation> ve) {
        for (final MappedField mf : mc.getPersistenceFields()) {
            check(mc, mf, ve);
        }
    }

    protected abstract void check(MappedClass mc, MappedField mf, Set<ConstraintViolation> ve);

}
