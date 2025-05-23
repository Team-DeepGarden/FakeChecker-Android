// Generated by view binder compiler. Do not edit!
package com.example.deepfake.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.deepfake.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentVideoBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView ivLogo;

  @NonNull
  public final AppCompatButton videoBtnFile;

  @NonNull
  public final ImageView videoIvPlay;

  @NonNull
  public final ProgressBar videoPbUpload;

  @NonNull
  public final TextView videoTvResult;

  @NonNull
  public final TextView videoTvResultreturn;

  @NonNull
  public final TextView videoTvTitle;

  @NonNull
  public final View videoViewLine;

  @NonNull
  public final View videoViewLine2;

  private FragmentVideoBinding(@NonNull ConstraintLayout rootView, @NonNull ImageView ivLogo,
      @NonNull AppCompatButton videoBtnFile, @NonNull ImageView videoIvPlay,
      @NonNull ProgressBar videoPbUpload, @NonNull TextView videoTvResult,
      @NonNull TextView videoTvResultreturn, @NonNull TextView videoTvTitle,
      @NonNull View videoViewLine, @NonNull View videoViewLine2) {
    this.rootView = rootView;
    this.ivLogo = ivLogo;
    this.videoBtnFile = videoBtnFile;
    this.videoIvPlay = videoIvPlay;
    this.videoPbUpload = videoPbUpload;
    this.videoTvResult = videoTvResult;
    this.videoTvResultreturn = videoTvResultreturn;
    this.videoTvTitle = videoTvTitle;
    this.videoViewLine = videoViewLine;
    this.videoViewLine2 = videoViewLine2;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentVideoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentVideoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_video, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentVideoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv_logo;
      ImageView ivLogo = ViewBindings.findChildViewById(rootView, id);
      if (ivLogo == null) {
        break missingId;
      }

      id = R.id.video_btn_file;
      AppCompatButton videoBtnFile = ViewBindings.findChildViewById(rootView, id);
      if (videoBtnFile == null) {
        break missingId;
      }

      id = R.id.video_iv_play;
      ImageView videoIvPlay = ViewBindings.findChildViewById(rootView, id);
      if (videoIvPlay == null) {
        break missingId;
      }

      id = R.id.video_pb_upload;
      ProgressBar videoPbUpload = ViewBindings.findChildViewById(rootView, id);
      if (videoPbUpload == null) {
        break missingId;
      }

      id = R.id.video_tv_result;
      TextView videoTvResult = ViewBindings.findChildViewById(rootView, id);
      if (videoTvResult == null) {
        break missingId;
      }

      id = R.id.video_tv_resultreturn;
      TextView videoTvResultreturn = ViewBindings.findChildViewById(rootView, id);
      if (videoTvResultreturn == null) {
        break missingId;
      }

      id = R.id.video_tv_title;
      TextView videoTvTitle = ViewBindings.findChildViewById(rootView, id);
      if (videoTvTitle == null) {
        break missingId;
      }

      id = R.id.video_view_line;
      View videoViewLine = ViewBindings.findChildViewById(rootView, id);
      if (videoViewLine == null) {
        break missingId;
      }

      id = R.id.video_view_line2;
      View videoViewLine2 = ViewBindings.findChildViewById(rootView, id);
      if (videoViewLine2 == null) {
        break missingId;
      }

      return new FragmentVideoBinding((ConstraintLayout) rootView, ivLogo, videoBtnFile,
          videoIvPlay, videoPbUpload, videoTvResult, videoTvResultreturn, videoTvTitle,
          videoViewLine, videoViewLine2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
