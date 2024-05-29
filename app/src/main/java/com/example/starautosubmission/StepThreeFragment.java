package com.example.starautosubmission;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class StepThreeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<SubmissionPreview> previewList;
    private SubmissionAdapter adapter;
    private static final String PREFS_NAME = "SubmissionPrefs";
    private String authorName;
    private static final String TAG = "StepThreeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_three, container, false);

        recyclerView = view.findViewById(R.id.rv_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 从云服务器读取用户名
        authorName = BmobUser.getCurrentUser(BmobUser.class).getUsername();

        // 加载预览信息
        loadPreviews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次加载页面时重新读取信息
        loadPreviews();
    }

    private void loadPreviews() {
        previewList = generatePreviews();
        adapter = new SubmissionAdapter(previewList, this::showPreviewDialog);
        recyclerView.setAdapter(adapter);
    }

    private List<SubmissionPreview> generatePreviews() {
        // Load submission info from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String title = sharedPreferences.getString("title", "");
        String description = sharedPreferences.getString("description", "");
        String location = sharedPreferences.getString("location", "");
        String date = sharedPreferences.getString("date", "");
        String camera = sharedPreferences.getString("camera", "");
        String lens = sharedPreferences.getString("lens", "");
        String aperture = sharedPreferences.getString("aperture", "");
        String shutter = sharedPreferences.getString("shutter", "");
        String iso = sharedPreferences.getString("iso", "");
        String others = sharedPreferences.getString("others", "");

        List<SubmissionPreview> previews = new ArrayList<>();
        previews.add(new SubmissionPreview("CSVA", generateCSVAPreview(title, description, location, date, camera, lens, aperture, shutter, iso, others),
                "csvastars@163.com", authorName + "-" + title, authorName + "-" + title + ".jpg"));
        previews.add(new SubmissionPreview("夜空中国", generateYeKongZhongGuoPreview(title, description, location, date, camera, lens, aperture, shutter, iso, others),
                "Steed@Mounstar.com", "【作品投稿】- " + title, title + ".jpg"));
        previews.add(new SubmissionPreview("中国国家天文", generateZhongGuoGuoJiaTianWenPreview(title, description, location, date, camera, lens, aperture, shutter, iso, others),
                "cinastronomy@163.com", "【作品投稿】- " + title, title + ".jpg"));
        previews.add(new SubmissionPreview("北京天文馆", generateBeiJingTianWenGuanPreview(title, description, location, date, camera, lens, aperture, shutter, iso, others),
                "apod@bjp.org.cn", "“星空之美”影像作品投稿 - " + authorName, authorName + "-" + title + ".jpg"));

        return previews;
    }

    private String generateCSVAPreview(String title, String description, String location, String date, String camera, String lens, String aperture, String shutter, String iso, String others) {
        return "作品标题：" + title + "\n" +
                "猎光者署名：" + authorName + "\n" +
                "猎光者语：" + description + "\n" +
                "拍摄时间：" + date + "\n" +
                "星空打卡地：" + location + "\n" +
                "拍摄器材：" + camera + " + " + lens + "\n" +
                "拍摄参数：" + aperture + ", " + shutter + ", ISO " + iso + ", " + others;
    }

    private String generateYeKongZhongGuoPreview(String title, String description, String location, String date, String camera, String lens, String aperture, String shutter, String iso, String others) {
        return "作品文件名：.jpg\n" +
                "署名：" + authorName + "\n" +
                "作品标题：" + title + "\n" +
                "文字说明：" + description + "\n" +
                "拍摄日期：" + date + "\n" +
                "拍摄地点：" + location + "\n" +
                "拍摄参数：" + camera + " + " + lens + ", " + aperture + ", " + shutter + ", ISO " + iso + ", " + others;
    }

    private String generateZhongGuoGuoJiaTianWenPreview(String title, String description, String location, String date, String camera, String lens, String aperture, String shutter, String iso, String others) {
        return "作品名称：" + title + "\n" +
                "作者名字：" + authorName + "\n" +
                "拍摄器材：" + camera + " + " + lens + "\n" +
                "拍摄参数：" + aperture + ", " + shutter + ", ISO " + iso + ", " + others + "\n" +
                "拍摄地点：" + location + "\n" +
                "拍摄日期：" + date + "\n" +
                "图片说明：" + description;
    }

    private String generateBeiJingTianWenGuanPreview(String title, String description, String location, String date, String camera, String lens, String aperture, String shutter, String iso, String others) {
        return "作品名称：" + title + "\n" +
                "作者名字：" + authorName + "\n" +
                "拍摄日期：" + date + "\n" +
                "拍摄地点：" + location + "\n" +
                "拍摄器材：" + camera + " + " + lens + "\n" +
                "拍摄参数：" + aperture + ", " + shutter + ", ISO " + iso + ", " + others + "\n" +
                "图片说明：" + description;
    }

    private void showPreviewDialog(SubmissionPreview preview) {
        PreviewDialogFragment dialog = PreviewDialogFragment.newInstance(preview);
        dialog.show(getChildFragmentManager(), "PreviewDialog");
    }

    public static class PreviewDialogFragment extends DialogFragment {

        private static final String ARG_PREVIEW = "preview";
        private EditText etEmailGreeting;
        private EditText etEmailBody;
        private EditText etEmailClosing;
        private CheckBox cbTest;
        private EditText etTestEmail;
        private static final String TAG = "PreviewDialogFragment";

        public static PreviewDialogFragment newInstance(SubmissionPreview preview) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_PREVIEW, preview);
            PreviewDialogFragment fragment = new PreviewDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_preview, container, false);

            TextView tvReceiverEmail = view.findViewById(R.id.tv_receiver_email);
            TextView tvSubject = view.findViewById(R.id.tv_subject);
            TextView tvAttachment = view.findViewById(R.id.tv_attachment);
            etEmailGreeting = view.findViewById(R.id.et_email_greeting);
            etEmailBody = view.findViewById(R.id.et_email_body);
            etEmailClosing = view.findViewById(R.id.et_email_closing);
            cbTest = view.findViewById(R.id.cb_test);
            etTestEmail = view.findViewById(R.id.et_test_email);
            Button btnSend = view.findViewById(R.id.btn_send);

            SubmissionPreview preview = (SubmissionPreview) getArguments().getSerializable(ARG_PREVIEW);
            tvReceiverEmail.setText(preview.getReceiverEmail());
            tvSubject.setText(preview.getSubject());
            tvAttachment.setText(preview.getAttachment());
            etEmailBody.setText(preview.getContent());

            btnSend.setOnClickListener(v -> sendEmail(preview));

            return view;
        }

        private void sendEmail(SubmissionPreview preview) {
            String recipient = cbTest.isChecked() ? etTestEmail.getText().toString().trim() : preview.getReceiverEmail();
            String subject = preview.getSubject();
            String body = etEmailGreeting.getText().toString().trim() + "\n\n" + etEmailBody.getText().toString().trim() + "\n\n" + etEmailClosing.getText().toString().trim();

            // 获取 StepOneFragment 中的图片 URI
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE);
            String savedUriString = sharedPreferences.getString("imageUri", null);
            Uri attachmentUri = savedUriString != null ? Uri.parse(savedUriString) : null;

            // 发送邮件
            MailSender mailSender = new MailSender(getContext());
            mailSender.sendEmail(recipient, subject, body, preview.getAttachment(), attachmentUri);
        }
    }
}
