// Generated by view binder compiler. Do not edit!
package com.example.deepfake.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

public final class ActivityStartBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final AppCompatButton startBtnGuestlogin;

  @NonNull
  public final Button startBtnLogin;

  @NonNull
  public final Button startBtnSignup;

  @NonNull
  public final LinearLayout startLlIntro;

  private ActivityStartBinding(@NonNull ConstraintLayout rootView,
      @NonNull AppCompatButton startBtnGuestlogin, @NonNull Button startBtnLogin,
      @NonNull Button startBtnSignup, @NonNull LinearLayout startLlIntro) {
    this.rootView = rootView;
    this.startBtnGuestlogin = startBtnGuestlogin;
    this.startBtnLogin = startBtnLogin;
    this.startBtnSignup = startBtnSignup;
    this.startLlIntro = startLlIntro;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityStartBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityStartBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_start, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityStartBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.start_btn_guestlogin;
      AppCompatButton startBtnGuestlogin = ViewBindings.findChildViewById(rootView, id);
      if (startBtnGuestlogin == null) {
        break missingId;
      }

      id = R.id.start_btn_login;
      Button startBtnLogin = ViewBindings.findChildViewById(rootView, id);
      if (startBtnLogin == null) {
        break missingId;
      }

      id = R.id.start_btn_signup;
      Button startBtnSignup = ViewBindings.findChildViewById(rootView, id);
      if (startBtnSignup == null) {
        break missingId;
      }

      id = R.id.start_ll_intro;
      LinearLayout startLlIntro = ViewBindings.findChildViewById(rootView, id);
      if (startLlIntro == null) {
        break missingId;
      }

      return new ActivityStartBinding((ConstraintLayout) rootView, startBtnGuestlogin,
          startBtnLogin, startBtnSignup, startLlIntro);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
