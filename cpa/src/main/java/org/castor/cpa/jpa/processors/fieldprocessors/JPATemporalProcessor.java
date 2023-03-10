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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import javax.persistence.Temporal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.core.annotationprocessing.AnnotationTargetException;
import org.castor.core.nature.BaseNature;
import org.castor.cpa.jpa.natures.JPAFieldNature;
import org.castor.cpa.jpa.processors.BaseJPAAnnotationProcessor;

/**
 * @author <a href="mailto:wguttmn AT codehaus DOT org">Werner Guttmann</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 */
public class JPATemporalProcessor extends BaseJPAAnnotationProcessor {
    //-----------------------------------------------------------------------------------

    private final Log _log = LogFactory.getLog(this.getClass());
    
    //-----------------------------------------------------------------------------------

    public Class<? extends Annotation> forAnnotationClass() {
        return Temporal.class;
    }

    public <I extends BaseNature, A extends Annotation> boolean processAnnotation(
            final I info, final A annotation, final AnnotatedElement target)
            throws AnnotationTargetException {
        if ((info instanceof JPAFieldNature)
                && (annotation instanceof Temporal)
                && target.isAnnotationPresent(Temporal.class)) {
            _log.debug("processing field annotation " + annotation.toString());

            final JPAFieldNature jpaFieldNature = (JPAFieldNature) info;
            final Temporal temporal = (Temporal) annotation;
            jpaFieldNature.setTemporalType(temporal.value());
            return true;
        }
        return false;
    }

    //-----------------------------------------------------------------------------------
}
