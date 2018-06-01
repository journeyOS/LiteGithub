/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.github.ui.activity.main;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.journeyOS.base.Constant;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.userprovider.AuthUser;
import com.journeyOS.core.api.userprovider.IAuthUserProvider;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.base.StatusDataResource;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.github.R;
import com.journeyOS.github.SlidingDrawer;
import com.journeyOS.github.entity.User;
import com.journeyOS.github.ui.activity.profile.ProfileModel;
import com.journeyOS.github.ui.activity.search.SearchActivity;
import com.journeyOS.github.ui.adapter.MainPageAdapter;
import com.journeyOS.github.ui.fragment.profile.ProfileInfoFragment;
import com.journeyOS.github.ui.fragment.repos.ReposFragment;
import com.journeyOS.github.ui.fragment.settings.SettingsFragment;

import butterknife.BindView;

public class GithubActivity extends BaseActivity implements SlidingDrawer.OnItemSelectedListener {
    static final String TAG = GithubActivity.class.getSimpleName();

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.frame_container)
    FrameLayout mFragmentContainer;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    MainPageAdapter mAdapter;

    GithubModel mGithubModel;
    Context mContext;

    Bundle mBundle;
    final Observer<AuthUser> authUserStatusObserver = new Observer<AuthUser>() {
        @Override
        public void onChanged(@Nullable AuthUser user) {
            handleAuthUserStatusObserver(user);
        }
    };

    //profile
    ProfileModel mProfileModel;
    final Observer<StatusDataResource> userStatusObserver = new Observer<StatusDataResource>() {
        @Override
        public void onChanged(@Nullable StatusDataResource statusDataResource) {
            handleUserStatusObserver(statusDataResource);
        }
    };

    @Override
    public void initBeforeView() {
        super.initBeforeView();
        mContext = CoreManager.getContext();
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.github_activity;
    }

    @Override
    public void initViews() {
        mCollapsingToolbarLayout.setTitleEnabled(false);
        UIUtils.setStatusBarColor(this, this.getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        mAdapter = new MainPageAdapter(this, getSupportFragmentManager());
    }


    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);
        mBundle = savedInstanceState;

        mGithubModel = ModelProvider.getModel(this, GithubModel.class);
        mGithubModel.searchAuthUser();
        mGithubModel.getAuthUserStatus().observe(this, authUserStatusObserver);

        //profile
        mProfileModel = ModelProvider.getModel(this, ProfileModel.class);
        mProfileModel.getUserStatus().observe(this, userStatusObserver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_github, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.search:
                SearchActivity.show(mContext);
                break;
            default:
                break;

        }
        return true;
    }

    @Override
    public void onItemSelected(int position) {
        handleItemSelected(position);
    }

    void handleAuthUserStatusObserver(AuthUser authUser) {
        CoreManager.setAuthUser(authUser);
        SlidingDrawer.getInstance(this).initDrawer(mBundle, mToolbar);
        SlidingDrawer.getInstance(this).setListener(this);
    }

    void loadFragment(Fragment fragment) {
        mFragmentContainer.setVisibility(View.VISIBLE);
        mTabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    void handleItemSelected(int position) {
        switch (position) {
            case Constant.MENU_PROFILE:
                mToolbar.setTitle(R.string.profile);
                AuthUser authUser = CoreManager.getAuthUser();
                if (BaseUtils.isNull(authUser)) {
                    CoreManager.getImpl(IAuthUserProvider.class).getUserWorkHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            AuthUser authUser = CoreManager.getImpl(IAuthUserProvider.class).getAuthUser();
                            mProfileModel.getUserInfo(authUser.loginId);
                        }
                    });
                } else {
                    mProfileModel.getUserInfo(authUser.loginId);
                }
//                ProfileActivity.show(mContext, BaseUtils.isNull(authUser) ? null : authUser.loginId);
                break;
            case Constant.MENU_REPOS:
                mToolbar.setTitle(R.string.my_repos);
                loadFragment(ReposFragment.newInstance(ReposFragment.ReposType.OWNED));
//                ContainerActivity.show(mContext, ReposFragment.ReposType.OWNED);
                break;
            case Constant.MENU_NOTIFICATION:
                break;
            case Constant.MENU_ISSUE:
                break;
            case Constant.MENU_SEARCH:
                SearchActivity.show(mContext);
                break;
            case Constant.MENU_STARRED:
                mToolbar.setTitle(R.string.starred);
                loadFragment(ReposFragment.newInstance(ReposFragment.ReposType.STARRED));
//                ContainerActivity.show(mContext, ReposFragment.ReposType.STARRED);
                break;
            case Constant.MENU_SETTINGS:
                mToolbar.setTitle(R.string.settings);
                loadFragment(SettingsFragment.newInstance(this));
                break;
            case Constant.MENU_ABOUT:
                break;

        }
    }

    //profile
    void handleUserStatusObserver(StatusDataResource statusDataResource) {
        switch (statusDataResource.status) {
            case SUCCESS:
                User user = (User) statusDataResource.data;
                if (!BaseUtils.isNull(user)) setupProfileViewPager(user);
                break;
            case ERROR:
                showShortToast(ToastType.ERROR, statusDataResource.message);
                break;
        }
    }

    void setupProfileViewPager(User user) {
        mAdapter.clearAll();
        mTabLayout.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mFragmentContainer.setVisibility(View.GONE);

        Pair<Fragment, Integer> profileInfoFragmentPair = new Pair<>(ProfileInfoFragment.newInstance(user), R.string.info);
        mAdapter.addFrag(profileInfoFragmentPair);

        Pair<Fragment, Integer> fileFragmentPair = new Pair<>(ReposFragment.newInstance(ReposFragment.ReposType.STARRED), R.string.starred);
        mAdapter.addFrag(fileFragmentPair);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        for (int index = 0; index < mAdapter.getCount(); index++) {
            mTabLayout.getTabAt(index).setCustomView(mAdapter.getTabView(index, mTabLayout));
        }

        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mViewPager.setCurrentItem(0);
    }

}