package com.zzzmode.appopsx.ui.main.group;

import android.content.Context;
import android.preference.PreferenceManager;

import com.zzzmode.appopsx.constraint.AppOpsMode;
import com.zzzmode.appopsx.ui.core.Helper;
import com.zzzmode.appopsx.ui.model.PermissionChildItem;
import com.zzzmode.appopsx.ui.model.PermissionGroup;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceSingleObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

/**
 * Created by zl on 2017/7/17.
 */

class PermGroupPresenter {

    private static final String TAG = "PermGroupPresenter";
    private IPermGroupView mView;

    private Context context;

    private ResourceSingleObserver<List<PermissionGroup>> subscriber;

    private boolean loadSuccess = false;

    PermGroupPresenter(IPermGroupView mView, Context context) {
        this.mView = mView;
        this.context = context;
    }


    void loadPerms() {
        boolean showSysApp = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("show_sysapp", false);

        boolean showIgnored = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("key_g_show_ignored", false);

        boolean alwaysShownPerm = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("key_always_shown_perms", false);

        subscriber = new ResourceSingleObserver<List<PermissionGroup>>() {
            @Override
            public void onSuccess(List<PermissionGroup> value) {
                loadSuccess = true;
                mView.showList(value);
            }

            @Override
            public void onError(Throwable e) {
                mView.showError(e);
            }

        };

        Helper.getPermissionGroup(context, showSysApp, showIgnored, alwaysShownPerm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    boolean isLoadSuccess() {
        return loadSuccess;
    }

    void changeMode(final int groupPosition, final int childPosition,
                    final PermissionChildItem info, final int prevMode) {
        Helper.setMode(context, info.appInfo.packageName, info.opEntryInfo)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        if (prevMode != info.opEntryInfo.mode) {
                            if (prevMode == AppOpsMode.MODE_IGNORED) {
                                mView.changeTitle(groupPosition, childPosition, 1);
                            } else if (info.opEntryInfo.mode == AppOpsMode.MODE_IGNORED) {
                                mView.changeTitle(groupPosition, childPosition, -1);
                            } else {
                                mView.changeTitle(groupPosition, childPosition, 0);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            mView.refreshItem(groupPosition, childPosition);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                });
    }


    void destroy() {
        if (subscriber != null && !subscriber.isDisposed()) {
            subscriber.dispose();
        }
    }
}
