package ovfaves.sport.com;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import io.michaelrocks.paranoid.Obfuscate;
import ovfaves.sport.com.databinding.FragmentFavPPBinding;
@Obfuscate
public class FavPPFragment extends Fragment {

    private FragmentFavPPBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavPPBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.bBackToMain.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.navigation_favMenu));
    }



}