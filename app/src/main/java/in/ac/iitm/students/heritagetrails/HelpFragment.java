package in.ac.iitm.students.heritagetrails;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * Created by Nitin on 04-02-2017.
 */

public class HelpFragment extends Fragment {
    private int drawable_id;

    public HelpFragment getFragment(int num) {
        HelpFragment f = new HelpFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("drawable_id", num);
        f.setArguments(args);

        return f;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawable_id = getArguments().getInt("drawable_id");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);
        Drawable drawable = null;

        if (drawable_id == 0) {
            drawable = ContextCompat.getDrawable(getActivity(), R.drawable.help_image_1);

        } else if (drawable_id == 1) {
            drawable = ContextCompat.getDrawable(getActivity(), R.drawable.help_image_2);

        } else if (drawable_id == 2) {
            drawable = ContextCompat.getDrawable(getActivity(), R.drawable.help_image_3);
        }
        //Toast.makeText(getActivity(), drawable_id+"", Toast.LENGTH_SHORT).show();

        ImageView imageView = (ImageView) v.findViewById(R.id.iv_help_fragment);
        imageView.setImageDrawable(drawable);


        return v;
    }

}
