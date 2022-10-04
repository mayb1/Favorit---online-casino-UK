package ovfaves.sport.com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import io.michaelrocks.paranoid.Obfuscate;
import ovfaves.sport.com.databinding.FragmentFaveChooseBalanceBinding;
@Obfuscate
public class FaveChooseBalanceFragment extends Fragment {

    private FragmentFaveChooseBalanceBinding binding;

    private boolean isFavButtonPressed = false;

    private int newBalance;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding = FragmentFaveChooseBalanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences favSp = Objects.requireNonNull(getActivity()).getSharedPreferences("savedData", Context.MODE_PRIVATE);

        int favPointsData = favSp.getInt("savedData", 0);

        if (favPointsData == 0){
            binding.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                switch (i) {
                    case R.id.rbPoints5000:
                        newBalance = 5000;
                        isFavButtonPressed = true;
                        break;
                    case R.id.rbPoints7500:
                        newBalance = 7500;
                        isFavButtonPressed = true;
                        break;
                    case R.id.rbPoints10000:
                        newBalance = 10000;
                        isFavButtonPressed = true;
                        break;
                }

                Toast.makeText(getContext(), "Ok, your balance will be " + newBalance,
                        Toast.LENGTH_SHORT).show();


                favSp.edit()
                        .putInt("savedData", newBalance)
                        .apply();

            });

            binding.bStartFavGame.setOnClickListener(view1 -> {
                if (isFavButtonPressed){
                    Navigation.findNavController(requireView()).navigate(R.id.navigation_favBaraban);
                } else {
                    Toast.makeText(getContext(), "You need to choose your balance!",
                            Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            Navigation.findNavController(requireView()).navigate(R.id.navigation_favBaraban);
        }

    }



}