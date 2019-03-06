//package io.plactal.eoscommander.di.component;
//
//import android.app.Application;
//import android.content.Context;
//
//import io.plactal.eoscommander.app.EosCommanderApp;
//import io.plactal.eoscommander.data.EoscDataManager;
//import io.plactal.eoscommander.di.module.AppModule;
//import oyz.com.eosapi.net.HostInterceptor;
//
//public class DaggerAppComponent implements AppComponent {
//
//    public static DaggerAppComponentBuilder builder() {
//        return new DaggerAppComponentBuilder();
//    }
//
//    public static class DaggerAppComponentBuilder {
//        AppModule appModule;
//
//        public DaggerAppComponentBuilder appModule(AppModule appModule) {
//            this.appModule = appModule;
//            return this;
//        }
//
//        public DaggerAppComponent build() {
//            return new DaggerAppComponent(this.appModule);
//        }
//    }
//
//    final AppModule appModule;
//
//    public  DaggerAppComponent(AppModule appModule){
//        this.appModule = appModule;
//    }
//
//    @Override
//    public void inject(EosCommanderApp eosCommanderApp) {
//
//    }
//
//    @Override
//    public Context context() {
//        return null;
//    }
//
//    @Override
//    public Application application() {
//        return null;
//    }
//
//    @Override
//    public EoscDataManager dataManager() {
//        return null;
//    }
//
//    @Override
//    public HostInterceptor hostInterceptor() {
//        return null;
//    }
//}
