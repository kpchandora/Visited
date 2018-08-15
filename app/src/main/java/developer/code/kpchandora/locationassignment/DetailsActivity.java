package developer.code.kpchandora.locationassignment;

import android.os.Bundle;
import android.widget.RelativeLayout;

public class DetailsActivity extends RootAnimActivity {

    public static final int UNKNOWN_ADDRESS_ICON = 5;
    public static final int KNOWN_ADDRESS_ICON = 4;
    public static final String ICON_TAG = "icon_tag";

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        relativeLayout = findViewById(R.id.rela_round_big);

        if (getIntent() != null){

            int tag = getIntent().getIntExtra(ICON_TAG, UNKNOWN_ADDRESS_ICON);
            switch (tag){
                case UNKNOWN_ADDRESS_ICON:
                    relativeLayout.setBackgroundResource(R.drawable.unknown_address_icon);
                    break;
                case KNOWN_ADDRESS_ICON:
                    relativeLayout.setBackgroundResource(R.drawable.location_icon);
                    break;
            }

        }

    }
}
