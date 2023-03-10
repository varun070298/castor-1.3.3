/*
 * Copyright 2006 Werner Guttmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.castor.cpa.jpa.processors.fieldprocessors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.core.annotationprocessing.AnnotationTargetException;
import org.castor.core.nature.BaseNature;
import org.castor.cpa.jpa.natures.JPAFieldNature;
import org.castor.cpa.jpa.processors.BaseJPAAnnotationProcessor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author <a href=" mailto:wguttmn AT codehaus DOT org">Werner Guttmann</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 */
public class JPAEnumeratedProcessor extends BaseJPAAnnotationProcessor {
    //-----------------------------------------------------------------------------------

    private final Log _log = LogFactory.getLog(getClass());
    
    //-----------------------------------------------------------------------------------

    public Class<? extends Annotation> forAnnotationClass() {
        return Enumerated.class;
    }

    public <I extends BaseNature, A extends Annotation> boolean processAnnotation(
            final I info, final A annotation, final AnnotatedElement target)
            throws AnnotationTargetException {
        if ((info instanceof JPAFieldNature)
                && (annotation instanceof Enumerated)
                && target.isAnnotationPresent(Enumerated.class)) {
            _log.debug("processing field annotation " + annotation.toString());

            final JPAFieldNature jpaFieldNature = (JPAFieldNature) info;
            if (((Enumerated) annotation).value() == EnumType.STRING) {
                jpaFieldNature.setStringEnumType(true);
            }
            return true;
        }
        return false;
    }

    //-----------------------------------------------------------------------------------
}
