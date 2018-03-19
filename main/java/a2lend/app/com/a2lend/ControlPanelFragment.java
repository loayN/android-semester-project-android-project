package a2lend.app.com.a2lend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Igbar on 1/22/2018.
 */

public class ControlPanelFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_panel,null);
        //super.onCreateView(inflater, container, savedInstanceState);
        // on create
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  created
        Toast.makeText(getContext(), "ControlPanelFragment ", Toast.LENGTH_SHORT).show();


    }

}
