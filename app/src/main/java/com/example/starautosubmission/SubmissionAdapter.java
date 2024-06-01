package com.example.starautosubmission;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.ViewHolder> {

    private List<SubmissionPreview> previewList;
    private OnPreviewClickListener previewClickListener;

    public SubmissionAdapter(List<SubmissionPreview> previewList, OnPreviewClickListener previewClickListener) {
        this.previewList = previewList;
        this.previewClickListener = previewClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_submission_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubmissionPreview preview = previewList.get(position);
        holder.tvFormat.setText(preview.getFormat());
        holder.tvContent.setText(preview.getContent());

        holder.btnPreview.setOnClickListener(v -> previewClickListener.onPreviewClick(preview));
        holder.btnCopy.setOnClickListener(v -> {
            // 实现复制内容逻辑
        });
    }

    @Override
    public int getItemCount() {
        return previewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFormat, tvContent;
        Button btnPreview, btnCopy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFormat = itemView.findViewById(R.id.tv_format);
            tvContent = itemView.findViewById(R.id.tv_content);
            btnPreview = itemView.findViewById(R.id.btn_preview);
            btnCopy = itemView.findViewById(R.id.btn_copy);
        }
    }

    public interface OnPreviewClickListener {
        void onPreviewClick(SubmissionPreview preview);
    }
}
