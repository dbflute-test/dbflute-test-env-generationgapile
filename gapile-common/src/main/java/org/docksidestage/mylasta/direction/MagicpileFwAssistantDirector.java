/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.mylasta.direction;

import java.util.List;

import javax.annotation.Resource;

import org.docksidestage.mylasta.direction.sponsor.MagicpileActionAdjustmentProvider;
import org.docksidestage.mylasta.direction.sponsor.MagicpileApiFailureHook;
import org.docksidestage.mylasta.direction.sponsor.MagicpileCookieResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.MagicpileCurtainBeforeHook;
import org.docksidestage.mylasta.direction.sponsor.MagicpileJsonResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.MagicpileMailDeliveryDepartmentCreator;
import org.docksidestage.mylasta.direction.sponsor.MagicpileSecurityResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.MagicpileTimeResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.MagicpileUserLocaleProcessProvider;
import org.docksidestage.mylasta.direction.sponsor.MagicpileUserTimeZoneProcessProvider;
import org.lastaflute.core.direction.CachedFwAssistantDirector;
import org.lastaflute.core.direction.FwAssistDirection;
import org.lastaflute.core.direction.FwCoreDirection;
import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.core.security.OneWayCryptographer;
import org.lastaflute.db.dbflute.classification.ListedClassificationProvider;
import org.lastaflute.db.direction.FwDbDirection;
import org.lastaflute.web.direction.FwWebDirection;

/**
 * @author jflute
 */
public abstract class MagicpileFwAssistantDirector extends CachedFwAssistantDirector {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private MagicpileConfig magicpileConfig;

    // ===================================================================================
    //                                                                              Assist
    //                                                                              ======
    @Override
    protected void prepareAssistDirection(FwAssistDirection direction) {
        direction.directConfig(nameList -> setupAppConfig(nameList), "magicpile_config.properties", "magicpile_env.properties");
    }

    protected abstract void setupAppConfig(List<String> nameList);

    // ===================================================================================
    //                                                                               Core
    //                                                                              ======
    @Override
    protected void prepareCoreDirection(FwCoreDirection direction) {
        // this configuration is on magicpile_env.properties because this is true only when development
        direction.directDevelopmentHere(magicpileConfig.isDevelopmentHere());

        // titles of the application for logging are from configurations
        direction.directLoggingTitle(magicpileConfig.getDomainTitle(), magicpileConfig.getEnvironmentTitle());

        // this configuration is on sea_env.properties because it has no influence to production
        // even if you set true manually and forget to set false back
        direction.directFrameworkDebug(magicpileConfig.isFrameworkDebug()); // basically false

        // you can add your own process when your application's curtain before
        direction.directCurtainBefore(createCurtainBeforeHook());

        direction.directSecurity(createSecurityResourceProvider());
        direction.directTime(createTimeResourceProvider());
        direction.directJson(createJsonResourceProvider());
        direction.directMail(createMailDeliveryDepartmentCreator().create());
    }

    protected MagicpileCurtainBeforeHook createCurtainBeforeHook() {
        return new MagicpileCurtainBeforeHook();
    }

    protected MagicpileSecurityResourceProvider createSecurityResourceProvider() { // #change_it_first
        final InvertibleCryptographer inver = InvertibleCryptographer.createAesCipher("magicpile:dockside");
        final OneWayCryptographer oneWay = OneWayCryptographer.createSha256Cryptographer();
        return new MagicpileSecurityResourceProvider(inver, oneWay);
    }

    protected MagicpileTimeResourceProvider createTimeResourceProvider() {
        return new MagicpileTimeResourceProvider(magicpileConfig);
    }

    protected MagicpileJsonResourceProvider createJsonResourceProvider() {
        return new MagicpileJsonResourceProvider();
    }

    protected MagicpileMailDeliveryDepartmentCreator createMailDeliveryDepartmentCreator() {
        return new MagicpileMailDeliveryDepartmentCreator(magicpileConfig);
    }

    // ===================================================================================
    //                                                                                 DB
    //                                                                                ====
    @Override
    protected void prepareDbDirection(FwDbDirection direction) {
        direction.directClassification(createListedClassificationProvider());
    }

    protected abstract ListedClassificationProvider createListedClassificationProvider();

    // ===================================================================================
    //                                                                                Web
    //                                                                               =====
    @Override
    protected void prepareWebDirection(FwWebDirection direction) {
        direction.directRequest(createUserLocaleProcessProvider(), createUserTimeZoneProcessProvider());
        direction.directCookie(createCookieResourceProvider());
        direction.directAdjustment(createActionAdjustmentProvider());
        direction.directMessage(nameList -> setupAppMessage(nameList), "magicpile_message", "magicpile_label");
        direction.directApiCall(createApiFailureHook());
    }

    protected MagicpileUserLocaleProcessProvider createUserLocaleProcessProvider() {
        return new MagicpileUserLocaleProcessProvider();
    }

    protected MagicpileUserTimeZoneProcessProvider createUserTimeZoneProcessProvider() {
        return new MagicpileUserTimeZoneProcessProvider();
    }

    protected MagicpileCookieResourceProvider createCookieResourceProvider() { // #change_it_first
        final InvertibleCryptographer cr = InvertibleCryptographer.createAesCipher("dockside:magicpile");
        return new MagicpileCookieResourceProvider(magicpileConfig, cr);
    }

    protected MagicpileActionAdjustmentProvider createActionAdjustmentProvider() {
        return new MagicpileActionAdjustmentProvider();
    }

    protected abstract void setupAppMessage(List<String> nameList);

    protected MagicpileApiFailureHook createApiFailureHook() {
        return new MagicpileApiFailureHook();
    }
}
