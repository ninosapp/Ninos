package in.ninos.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

import in.ninos.R;

/**
 * Created by FAMILY on 02-03-2018.
 */

public class PolicyFragment extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_policy, null);

        contentView.findViewById(R.id.tv_policy_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent policyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.policy_link)));
                startActivity(policyIntent);
                dismiss();
            }
        });

        dialog.setContentView(contentView);
    }
}
