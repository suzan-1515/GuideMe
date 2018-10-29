package com.guideme.guideme.review;


import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.guideme.guideme.R;
import com.guideme.guideme.model.User;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRatingBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends BottomSheetDialogFragment {

    private OnActionCallBack callBack;

    public static ReviewFragment newInstance(final User user) {
        ReviewFragment reviewFragment = new ReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        reviewFragment.setArguments(bundle);
        return reviewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_review, container, false);

        Bundle arguments = getArguments();
        if (arguments == null) dismiss();

        User user = (User) arguments.getSerializable("user");
        if (user == null) dismiss();

        //Get the content View
        final AppCompatImageView avatar = contentView.findViewById(R.id.avatar);
        final AppCompatRatingBar ratingBar = contentView.findViewById(R.id.ratingbar);
        final TextInputEditText reviewInputField = contentView.findViewById(R.id.input_review);
        MaterialButton postButton = contentView.findViewById(R.id.post_button);
        MaterialButton cancelButton = contentView.findViewById(R.id.cancel_button);

        if (!TextUtils.isEmpty(user.getImage())) {
            Glide.with(contentView)
                    .load(user.getImage())
                    .into(avatar);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = reviewInputField.getEditableText().toString().trim();
                int rating = (int) ratingBar.getRating();

                if (callBack != null) {
                    callBack.onPostButtonClicked(v, review, rating);
                }
                dismiss();
            }
        });

        return contentView;
    }

    public void registerActionCallback(OnActionCallBack callBack) {
        this.callBack = callBack;
    }

    public interface OnActionCallBack {
        void onPostButtonClicked(View v, String review, int rating);
    }

}
