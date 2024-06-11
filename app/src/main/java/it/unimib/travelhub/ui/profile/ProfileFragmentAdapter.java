package it.unimib.travelhub.ui.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.ui.profile.OngoingTravelsFragment;
import it.unimib.travelhub.ui.profile.TerminatedTravelsFragment;

public class ProfileFragmentAdapter extends FragmentStateAdapter{

    private Travels travel;
    public ProfileFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new OngoingTravelsFragment();
        }else {
            return new TerminatedTravelsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
