package com.guideme.guideme.home;


import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.guideme.guideme.R;

import androidx.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class RadiusFragment extends BottomSheetDialogFragment {

    private static final int MAX_RADIUS = 1000;
    private OnActionCallBack callBack;

    public static RadiusFragment newInstance() {
        return new RadiusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_radius, container, false);

        //Get the content View
        final TextInputEditText radiusInputText = contentView.findViewById(R.id.input_radius);
        MaterialButton radiusButton = contentView.findViewById(R.id.radius_button);

        radiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String radius = radiusInputText.getEditableText().toString().trim();
                int rad = 0;
                if (TextUtils.isEmpty(radius)) {
                    radiusInputText.setError("Radius cannot be empty!");
                    radiusInputText.requestFocus();
                    return;
                } else {
                    rad = Integer.parseInt(radius);
                    if (rad > MAX_RADIUS) {
                        radiusInputText.setError("Maximun radius allowed is 1000M");
                        radiusInputText.requestFocus();
                        return;
                    }
                }


                if (callBack != null) {
                    callBack.onPostButtonClicked(v, rad);
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
        void onPostButtonClicked(View v, int radius);
    }

}
