package hexcore.vrgui;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by marcu on 17/06/2018.
 */

public class ConfirmAppInDevleopmentDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("VRMeeting - University of Nottingham - Team Hexcore").setMessage("\nVRMeeting is a prototype. \n\nBy continuing to use the app you accept that VRMeeting is not suitable for any production environment\n\n This project is subject to license, see documentation repository for more information").setPositiveButton("Ok, I agree.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }
}
