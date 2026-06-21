package com.example.canteen.controller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;


import com.example.canteen.R;
import com.example.canteen.data.setting.SettingsRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SettingsFragment extends Fragment {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final SettingsRepository repository;

    private ActivityResultLauncher<String> avatarPicker;

    private ImageView ivAvatar;
    private TextView tvName, tvUid, tvLastSync, tvServerAddress, tvDefaultApi, tvVersion, tvAboutMe, tvQQ;

    private TextInputEditText etServerAddress, etDefaultApi;

    private MaterialSwitch swAutoSync, swAllowMobileData;//, swReduceMotion, swCompactMode;

    private MaterialButton btnThemeSystem, btnThemeLight, btnThemeDark;

    private LinearLayout contentPersonal, contentData, contentAppearance, contentServer, contentAbout;
    private ImageView arrowPersonal, arrowData, arrowAppearance, arrowServer, arrowAbout;

    public SettingsFragment() {
        super(R.layout.fragment_settings);

        repository = SettingsRepository.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupPickAvatarLauncher();
        setupSectionToggles();
        setupActions();
        loadSettings();
        System.out.println("SettingsFragment created and initialized");
    }

    private void bindViews(View view) {
        ivAvatar = view.findViewById(R.id.ivSettingAvatar);
        tvName = view.findViewById(R.id.tvSettingName);
        tvUid = view.findViewById(R.id.tvSettingUid);
        tvLastSync = view.findViewById(R.id.tvLastSync);
        tvServerAddress = view.findViewById(R.id.tvServerAddressValue);
        tvDefaultApi = view.findViewById(R.id.tvDefaultApiValue);
        tvVersion = view.findViewById(R.id.tvVersion);
        tvAboutMe = view.findViewById(R.id.tvAboutMe);
        tvQQ = view.findViewById(R.id.tvQQ);

        etServerAddress = view.findViewById(R.id.etServerAddress);
        etDefaultApi = view.findViewById(R.id.etDefaultApi);

        swAutoSync = view.findViewById(R.id.swAutoSync);
        swAllowMobileData = view.findViewById(R.id.swAllowMobileData);
        //swReduceMotion = view.findViewById(R.id.swReduceMotion);
        //swCompactMode = view.findViewById(R.id.swCompactMode);

        btnThemeSystem = view.findViewById(R.id.btnThemeSystem);
        btnThemeLight = view.findViewById(R.id.btnThemeLight);
        btnThemeDark = view.findViewById(R.id.btnThemeDark);

        contentPersonal = view.findViewById(R.id.contentPersonal);
        contentData = view.findViewById(R.id.contentData);
        contentAppearance = view.findViewById(R.id.contentAppearance);
        contentServer = view.findViewById(R.id.contentServer);
        contentAbout = view.findViewById(R.id.contentAbout);

        arrowPersonal = view.findViewById(R.id.arrowPersonal);
        arrowData = view.findViewById(R.id.arrowData);
        arrowAppearance = view.findViewById(R.id.arrowAppearance);
        arrowServer = view.findViewById(R.id.arrowServer);
        arrowAbout = view.findViewById(R.id.arrowAbout);
    }

    private void setupPickAvatarLauncher() {
        avatarPicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) return;
                    // 这里改成你真实的头像上传/保存逻辑即可
                    disposables.add(
                            repository.updateAvatar(uri.toString())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        ivAvatar.setImageURI(uri);
                                    }, throwable -> showToast("修改头像失败"))
                    );
                }
        );
    }

    private void setupSectionToggles() {
        setupToggle(viewByIdSafe(R.id.headerPersonal), contentPersonal, arrowPersonal);
        setupToggle(viewByIdSafe(R.id.headerData), contentData, arrowData);
        setupToggle(viewByIdSafe(R.id.headerAppearance), contentAppearance, arrowAppearance);
        setupToggle(viewByIdSafe(R.id.headerServer), contentServer, arrowServer);
        setupToggle(viewByIdSafe(R.id.headerAbout), contentAbout, arrowAbout);

        // 默认全部收起
        //collapse(contentPersonal, arrowPersonal);
        //collapse(contentData, arrowData);
        //collapse(contentAppearance, arrowAppearance);
        //collapse(contentServer, arrowServer);
        //collapse(contentAbout, arrowAbout);
        //expand(contentPersonal, arrowPersonal);
        //expand(contentData, arrowData);
        //expand(contentAppearance, arrowAppearance);
        //expand(contentServer, arrowServer);
        //expand(contentAbout, arrowAbout);
    }

    private void setupToggle(View header, LinearLayout content, ImageView arrow) {
        header.setOnClickListener(v -> {
            if (content.getVisibility() == View.VISIBLE) {
                collapse(content, arrow);
            } else {
                expand(content, arrow);
            }
        });
    }

    private void expand(LinearLayout content, ImageView arrow) {
        content.setVisibility(View.VISIBLE);
        arrow.animate().rotation(180f).setDuration(180).start();
        System.out.println("Expanding section: " + content.getId());
    }

    private void collapse(LinearLayout content, ImageView arrow) {
        content.setVisibility(View.GONE);
        arrow.animate().rotation(0f).setDuration(180).start();
    }

    private View viewByIdSafe(int id) {
        return requireView().findViewById(id);
    }

    private void setupActions() {
        requireView().findViewById(R.id.btnModifyAvatar).setOnClickListener(v -> avatarPicker.launch("image/*"));
        requireView().findViewById(R.id.btnModifyName).setOnClickListener(v -> showRenameDialog());
        requireView().findViewById(R.id.btnDeleteAllAccountData).setOnClickListener(v -> showDeleteAccountDialog());

        requireView().findViewById(R.id.btnResyncData).setOnClickListener(v -> resyncData());
        requireView().findViewById(R.id.btnClearCache).setOnClickListener(v -> clearCache());
        requireView().findViewById(R.id.btnClearHistory).setOnClickListener(v -> clearHistory());

        swAutoSync.setOnCheckedChangeListener((buttonView, isChecked) -> saveLocalSetting(() -> repository.setAutoSync(isChecked)));
        swAllowMobileData.setOnCheckedChangeListener((buttonView, isChecked) -> saveLocalSetting(() -> repository.setAllowMobileData(isChecked)));
        //swReduceMotion.setOnCheckedChangeListener((buttonView, isChecked) -> saveLocalSetting(() -> repository.setReduceMotion(isChecked)));
        //swCompactMode.setOnCheckedChangeListener((buttonView, isChecked) -> saveLocalSetting(() -> repository.setCompactMode(isChecked)));

        btnThemeSystem.setOnClickListener(v -> applyThemeMode("SYSTEM"));
        btnThemeLight.setOnClickListener(v -> applyThemeMode("LIGHT"));
        btnThemeDark.setOnClickListener(v -> applyThemeMode("DARK"));

        requireView().findViewById(R.id.btnSaveServer).setOnClickListener(v -> saveServerSettings());

        requireView().findViewById(R.id.btnCheckUpdate).setOnClickListener(v -> checkUpdate());
        requireView().findViewById(R.id.btnOpenGithub).setOnClickListener(v -> openGithub());
    }

    private void loadSettings() {
        disposables.add(
                repository.getSettingsSnapshot()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(snapshot -> {
                            tvName.setText(snapshot.getUserName());
                            tvUid.setText("UID：" + snapshot.getUid());
                            tvLastSync.setText("上次同步时间：" + snapshot.getLastSyncTime());
                            tvServerAddress.setText(snapshot.getServerAddress());
                            tvDefaultApi.setText(snapshot.getDefaultApi());
                            tvVersion.setText("版本号：Beta1.0");// + BuildConfig.VERSION_NAME);
                            tvAboutMe.setText(snapshot.getAboutMe());
                            tvQQ.setText(snapshot.getQq());

                            etServerAddress.setText(snapshot.getServerAddress());
                            etDefaultApi.setText(snapshot.getDefaultApi());

                            swAutoSync.setChecked(snapshot.isAutoSync());
                            swAllowMobileData.setChecked(snapshot.isAllowMobileData());
                            //swReduceMotion.setChecked(snapshot.isReduceMotion());
                            //swCompactMode.setChecked(snapshot.isCompactMode());

                            // 头像占位
                            ivAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
                        }, throwable -> {
                            tvVersion.setText("版本号：");// + BuildConfig.VERSION_NAME);
                            tvName.setText("未加载到资料");
                            tvUid.setText("UID：-");
                            tvLastSync.setText("上次同步时间：-");
                        })
        );
        System.out.println("SettingsFragment: Started loading settings");
    }

    private void showRenameDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("输入新名称");

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("修改名称")
                .setView(input)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newName = input.getText() == null ? "" : input.getText().toString().trim();
                    if (newName.isEmpty()) {
                        showToast("名称不能为空");
                        return;
                    }
                    disposables.add(
                            repository.updateUserName(newName)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        tvName.setText(newName);
                                        showToast("名称已更新");
                                    }, throwable -> showToast("修改失败"))
                    );
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("删除本账号所有信息")
                .setMessage("此操作会清除本账号的本地与云端数据，且不可恢复。是否继续？")
                .setPositiveButton("确认删除", (dialog, which) -> {
                    disposables.add(
                            repository.deleteAllAccountData()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        showToast("已删除");
                                        // 这里可顺手跳到登录页或欢迎页
                                    }, throwable -> showToast("删除失败"))
                    );
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void resyncData() {
        disposables.add(
                repository.resyncData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            showToast("同步失败");
                            loadSettings();
                        }, throwable -> showToast("同步失败"))
        );
    }

    private void clearCache() {
        disposables.add(
                repository.clearCache()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> showToast("缓存已清除"), throwable -> showToast("清除失败"))
        );
    }

    private void clearHistory() {
        disposables.add(
                repository.clearHistory()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> showToast("历史记录已清除"), throwable -> showToast("清除失败"))
        );
    }

    private void saveServerSettings() {
        String serverAddress = etServerAddress.getText() == null ? "" : etServerAddress.getText().toString().trim();
        String defaultApi = etDefaultApi.getText() == null ? "" : etDefaultApi.getText().toString().trim();

        if (serverAddress.isEmpty()) {
            showToast("服务器地址不能为空");
            return;
        }
        if (defaultApi.isEmpty()) {
            showToast("默认 API 不能为空");
            return;
        }

        disposables.add(
                repository.saveServerSettings(serverAddress, defaultApi)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            tvServerAddress.setText(serverAddress);
                            tvDefaultApi.setText(defaultApi);
                            showToast("服务器设置已保存");
                        }, throwable -> showToast("保存失败"))
        );
    }

    private void applyThemeMode(String mode) {
        disposables.add(
                repository.setThemeMode(mode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> showToast("已切换为：" + mode), throwable -> showToast("切换失败"))
        );
    }

    private void checkUpdate() {
        // 这里先空实现，后面你接你的更新接口即可
        showToast("检查更新：占位逻辑");
    }

    private void openGithub() {
        String url = "https://github.com/CeobeLapland?tab=repositories";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showToast("无法打开 GitHub");
        }
    }

    private void saveLocalSetting(Runnable action) {
        disposables.add(
                repository.runLocalSettingAction(action)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                        }, throwable -> showToast("保存失败"))
        );
    }

    private void showToast(String msg) {
        System.out.println("Toast: " + msg);
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), msg, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}