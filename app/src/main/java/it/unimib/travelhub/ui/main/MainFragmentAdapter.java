package it.unimib.travelhub.ui.main;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.ui.profile.OngoingTravelsFragment;
import it.unimib.travelhub.ui.profile.ProfileFragment;
import it.unimib.travelhub.ui.profile.TerminatedTravelsFragment;

public class MainFragmentAdapter extends FragmentStateAdapter{

    private Travels travel;
    public MainFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HomeFragment();
        }else if (position == 1) {
            return new CommunityFragment();
        } else if (position == 2) {
            return new MapFragment();
        } else {
            return new ProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
