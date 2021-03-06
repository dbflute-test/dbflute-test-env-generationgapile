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

import org.docksidestage.mylasta.direction.sponsor.GapileActionAdjustmentProvider;
import org.docksidestage.mylasta.direction.sponsor.GapileApiFailureHook;
import org.docksidestage.mylasta.direction.sponsor.GapileCookieResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.GapileCurtainBeforeHook;
import org.docksidestage.mylasta.direction.sponsor.GapileJsonResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.GapileMailDeliveryDepartmentCreator;
import org.docksidestage.mylasta.direction.sponsor.GapileSecurityResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.GapileTimeResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.GapileUserLocaleProcessProvider;
import org.docksidestage.mylasta.direction.sponsor.GapileUserTimeZoneProcessProvider;
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
public abstract class GapileFwAssistantDirector extends CachedFwAssistantDirector {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private GapileConfig gapileConfig;

    // ===================================================================================
    //                                                                              Assist
    //                                                                              ======
    @Override
    protected void prepareAssistDirection(FwAssistDirection direction) {
        direction.directConfig(nameList -> setupAppConfig(nameList), "gapile_config.properties", "gapile_env.properties");
    }

    protected abstract void setupAppConfig(List<String> nameList);

    // ===================================================================================
    //                                                                               Core
    //                                                                              ======
    @Override
    protected void prepareCoreDirection(FwCoreDirection direction) {
        // this configuration is on gapile_env.properties because this is true only when development
        direction.directDevelopmentHere(gapileConfig.isDevelopmentHere());

        // titles of the application for logging are from configurations
        direction.directLoggingTitle(gapileConfig.getDomainTitle(), gapileConfig.getEnvironmentTitle());

        // this configuration is on sea_env.properties because it has no influence to production
        // even if you set true manually and forget to set false back
        direction.directFrameworkDebug(gapileConfig.isFrameworkDebug()); // basically false

        // you can add your own process when your application's curtain before
        direction.directCurtainBefore(createCurtainBeforeHook());

        direction.directSecurity(createSecurityResourceProvider());
        direction.directTime(createTimeResourceProvider());
        direction.directJson(createJsonResourceProvider());
        direction.directMail(createMailDeliveryDepartmentCreator().create());
    }

    protected GapileCurtainBeforeHook createCurtainBeforeHook() {
        return new GapileCurtainBeforeHook();
    }

    protected GapileSecurityResourceProvider createSecurityResourceProvider() { // #change_it_first
        final InvertibleCryptographer inver = InvertibleCryptographer.createAesCipher("gapile:dockside");
        final OneWayCryptographer oneWay = OneWayCryptographer.createSha256Cryptographer();
        return new GapileSecurityResourceProvider(inver, oneWay);
    }

    protected GapileTimeResourceProvider createTimeResourceProvider() {
        return new GapileTimeResourceProvider(gapileConfig);
    }

    protected GapileJsonResourceProvider createJsonResourceProvider() {
        return new GapileJsonResourceProvider();
    }

    protected GapileMailDeliveryDepartmentCreator createMailDeliveryDepartmentCreator() {
        return new GapileMailDeliveryDepartmentCreator(gapileConfig);
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
        direction.directMessage(nameList -> setupAppMessage(nameList), "gapile_message", "gapile_label");
        direction.directApiCall(createApiFailureHook());
    }

    protected GapileUserLocaleProcessProvider createUserLocaleProcessProvider() {
        return new GapileUserLocaleProcessProvider();
    }

    protected GapileUserTimeZoneProcessProvider createUserTimeZoneProcessProvider() {
        return new GapileUserTimeZoneProcessProvider();
    }

    protected GapileCookieResourceProvider createCookieResourceProvider() { // #change_it_first
        final InvertibleCryptographer cr = InvertibleCryptographer.createAesCipher("dockside:gapile");
        return new GapileCookieResourceProvider(gapileConfig, cr);
    }

    protected GapileActionAdjustmentProvider createActionAdjustmentProvider() {
        return new GapileActionAdjustmentProvider();
    }

    protected abstract void setupAppMessage(List<String> nameList);

    protected GapileApiFailureHook createApiFailureHook() {
        return new GapileApiFailureHook();
    }
}
