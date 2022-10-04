package ovfaves.sport.com;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

import io.michaelrocks.paranoid.Obfuscate;
import ovfaves.sport.com.databinding.FragmentFavBarabanBinding;
@Obfuscate
public class FavBarabanFragment extends Fragment {

    private FragmentFavBarabanBinding binding;

    private SharedPreferences favSp;

    private int favBetSize = START_BET, favScore;

    private final static int START_BET = 100;

    private ImageView[] favItems;

    private Animation favWinnerAnimations;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding = FragmentFavBarabanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favSp = Objects.requireNonNull(getActivity()).getSharedPreferences("savedData", Context.MODE_PRIVATE);

        favScore =  favSp.getInt("savedData", 0);
        binding.tvFavGamePoints.setText(String.valueOf(favScore));

        Button[] goldButtons = new Button[]{binding.bSlotBet100, binding.bSlotBet250, binding.bSlotBet500};
        for (Button goldButton : goldButtons) {
            goldButton.setOnClickListener(v -> {
                int checkScoreSize = Integer.parseInt(v.getTag().toString());
                if (favScore < checkScoreSize) {
                    Toast.makeText(getContext(), "You cant do it", Toast.LENGTH_SHORT).show();
                } else {
                    favBetSize = checkScoreSize;
                    Toast.makeText(getContext(), "Now your bet is  " + favBetSize, Toast.LENGTH_SHORT).show();
                }

            });
        }

        favItems = new ImageView[] {binding.ivFavItem1, binding.ivFavItem2, binding.ivFavItem3, binding.ivFavItem4, binding.ivFavItem5, binding.ivFavItem6,
                binding.ivFavItem7, binding.ivFavItem8, binding.ivFavItem9, binding.ivFavItem10, binding.ivFavItem11,
                binding.ivFavItem12, binding.ivFavItem13, binding.ivFavItem14, binding.ivFavItem15};

        favWinnerAnimations = AnimationUtils.loadAnimation(getContext(), R.anim.fav);

        binding.bStartGame.setOnClickListener(v -> startFavGame());


    }

    private void startFavGame(){
        binding.bStartGame.setEnabled(false);
        new CountDownTimer(3000, 100) {
            @Override
            public void onTick(long l) {
                int[] parSlotImgs = {R.drawable.ffav1, R.drawable.ffav2, R.drawable.ffav3,
                        R.drawable.ffav4, R.drawable.ffav5, R.drawable.ffav7};
                for (ImageView parSlotImg : favItems) {
                    int random = (int) (Math.random() * 6);
                    parSlotImg.setImageResource(parSlotImgs[random]);
                    parSlotImg.setTag(parSlotImgs[random]);
                }
            }

            @Override
            public void onFinish() {
                binding.bStartGame.setEnabled(true);
                checkSlotWinner();
            }
        }.start();
    }


    private void checkSlotWinner(){
        int fav1 = 0;
        int fav2 = 0;
        int fav3 = 0;
        int fav4 = 0;
        int fav5 = 0;
        int fav6 = 0;
        int favResult;

        for(ImageView favItem : favItems){
            if (favItem.getTag().equals(R.drawable.ffav1)) {
                fav1++;
            } else if (favItem.getTag().equals(R.drawable.ffav2)) {
                fav2++;
            } else if (favItem.getTag().equals(R.drawable.ffav3)) {
                fav3++;
            } else if (favItem.getTag().equals(R.drawable.ffav4)) {
                fav4++;
            } else if (favItem.getTag().equals(R.drawable.ffav5)) {
                fav5++;
            } else if (favItem.getTag().equals(R.drawable.ffav7)) {
                fav6++;
            }
        }

        int parWinner;
        if (fav1 > 5 && fav1 > fav2 && fav1 > fav3 && fav1 > fav4 && fav1 > fav5 && fav1 > fav6) {
            parWinner = 1;
        } else if (fav2 > 5 && fav2 > fav1 && fav2 > fav3 && fav2 > fav4 && fav2 > fav5 && fav2 > fav6) {
            parWinner = 2;
        } else if (fav3 > 5 && fav3 > fav1 && fav3 > fav2 && fav3 > fav4 && fav3 > fav5 && fav3 > fav6) {
            parWinner = 3;
        } else if (fav4 > 5 && fav4 > fav1 && fav4 > fav2 && fav4 > fav3 && fav4 > fav5 && fav4 > fav6) {
            parWinner = 4;
        } else if (fav5 > 5 && fav5 > fav1 && fav5 > fav2 && fav5 > fav3 && fav5 > fav4 && fav5 > fav6) {
            parWinner = 5;
        } else if (fav6 > 5 && fav6 > fav1 && fav6 > fav2 && fav6 > fav3 && fav6 > fav4 && fav6 > fav5) {
            parWinner = 6;
        } else {
            parWinner = 0;
        }

        switch (parWinner) {
            case 0:
                favResult = -favBetSize;
                break;
            case 1:
                favResult = favBetSize * 2;
                for(ImageView vegasItem : favItems){
                    if (vegasItem.getTag().equals(R.drawable.ffav1)){
                        vegasItem.startAnimation(favWinnerAnimations);
                    }
                }
                break;
            case 2:
                favResult = favBetSize * 3;
                for(ImageView vegasItem : favItems){
                    if (vegasItem.getTag().equals(R.drawable.ffav2)){
                        vegasItem.startAnimation(favWinnerAnimations);
                    }
                }
                break;
            case 3:
                favResult = favBetSize * 4;
                for(ImageView vegasItem : favItems){
                    if (vegasItem.getTag().equals(R.drawable.ffav3)){
                        vegasItem.startAnimation(favWinnerAnimations);
                    }
                }
                break;
            case 4:
                favResult = favBetSize * 5;
                for(ImageView vegasItem : favItems){
                    if (vegasItem.getTag().equals(R.drawable.ffav4)){
                        vegasItem.startAnimation(favWinnerAnimations);
                    }
                }
                break;
            case 5:
                favResult = favBetSize * 6;
                for(ImageView vegasItem : favItems){
                    if (vegasItem.getTag().equals(R.drawable.ffav5)){
                        vegasItem.startAnimation(favWinnerAnimations);
                    }
                }
                break;
            case 6:
                favResult = favBetSize * 7;
                for(ImageView vegasItem : favItems){
                    if (vegasItem.getTag().equals(R.drawable.ffav7)){
                        vegasItem.startAnimation(favWinnerAnimations);
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + parWinner);
        }

        updateParPoints(favResult);

    }

    private void updateParPoints(int resultValue){

        favScore += resultValue;

        if(favScore < favBetSize){
            Toast.makeText(getContext(), "You loose all your points. Lets add some", Toast.LENGTH_SHORT).show();
            favScore += 2500;
        }

        favSp.edit()
                .putInt("savedData", favScore)
                .apply();
        binding.tvFavGamePoints.setText(String.valueOf(favScore));

    }




}