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
import ovfaves.sport.com.databinding.FragmentFavMenuBinding;
@Obfuscate
public class FavMenuFragment extends Fragment {

    private FragmentFavMenuBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavMenuBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.bStartBalancePage.setOnClickListener(v0 -> Navigation.findNavController(requireView()).navigate(R.id.navigation_faveChooseBalance));
        binding.bReadPP.setOnClickListener(v1 -> Navigation.findNavController(requireView()).navigate(R.id.navigation_favPP));
        binding.bCloseApp.setOnClickListener(v2 -> requireActivity().finish());
    }

}