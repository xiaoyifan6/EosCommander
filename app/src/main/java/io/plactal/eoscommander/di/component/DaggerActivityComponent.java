//package io.plactal.eoscommander.di.component;
//
//import android.app.Notification;
//
//import io.plactal.eoscommander.di.module.ActivityModule;
//import io.plactal.eoscommander.ui.MainActivity;
//import io.plactal.eoscommander.ui.account.AccountMainFragment;
//import io.plactal.eoscommander.ui.account.create.CreateEosAccountDialog;
//import io.plactal.eoscommander.ui.account.info.InputAccountDialog;
//import io.plactal.eoscommander.ui.currency.CurrencyFragment;
//import io.plactal.eoscommander.ui.gettable.GetTableFragment;
//import io.plactal.eoscommander.ui.push.PushFragment;
//import io.plactal.eoscommander.ui.push.abiview.MsgInputActivity;
//import io.plactal.eoscommander.ui.settings.SettingsActivity;
//import io.plactal.eoscommander.ui.transfer.TransferFragment;
//import io.plactal.eoscommander.ui.wallet.WalletFragment;
//import io.plactal.eoscommander.ui.wallet.dlg.CreateWalletDialog;
//import io.plactal.eoscommander.ui.wallet.dlg.InputDataDialog;
//
//
//public class DaggerActivityComponent implements ActivityComponent {
//
//    public static class DaggerActivityComponentBuilder {
//
//        ActivityModule activityModule;
//        AppComponent appComponent;
//
//        public DaggerActivityComponentBuilder activityModule(ActivityModule activityModule) {
//            this.activityModule = activityModule;
//            return this;
//        }
//
//
//        public DaggerActivityComponentBuilder appComponent(AppComponent appComponent) {
//            this.appComponent = appComponent;
//            return this;
//        }
//
//        public ActivityComponent build() {
//            return  new DaggerActivityComponent(this.activityModule, this.appComponent);
//        }
//    }
//
//    public static DaggerActivityComponentBuilder builder() {
//        return new DaggerActivityComponentBuilder();
//    }
//
//    final ActivityModule activityModule;
//    final AppComponent appComponent;
//
//    public DaggerActivityComponent(ActivityModule activityModule,AppComponent appComponent ){
//        this.activityModule = activityModule;
//        this.appComponent = appComponent;
//    }
//
//    @Override
//    public void inject(MainActivity activity) {
//
//    }
//
//    @Override
//    public void inject(SettingsActivity activity) {
//
//    }
//
//    @Override
//    public void inject(AccountMainFragment fragment) {
//
//    }
//
//    @Override
//    public void inject(CreateEosAccountDialog dialog) {
//
//    }
//
//    @Override
//    public void inject(WalletFragment fragment) {
//
//    }
//
//    @Override
//    public void inject(PushFragment fragment) {
//
//    }
//
//    @Override
//    public void inject(GetTableFragment fragment) {
//
//    }
//
//    @Override
//    public void inject(CurrencyFragment fragment) {
//
//    }
//
//    @Override
//    public void inject(TransferFragment fragment) {
//
//    }
//
//    @Override
//    public void inject(InputDataDialog dialog) {
//
//    }
//
//    @Override
//    public void inject(CreateWalletDialog dialog) {
//
//    }
//
//    @Override
//    public void inject(InputAccountDialog dialog) {
//
//    }
//
//    @Override
//    public void inject(MsgInputActivity activity) {
//
//    }
//
//}
