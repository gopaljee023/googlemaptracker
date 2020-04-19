package herdum.com.tracker2;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetAddressIntentService extends IntentService {

    private static final String TAG = "GetAddressIntentService";
    private static final String ACTION_ADDRESS = "herdum.com.tracker2.action.ADDRESS";


    private static final String EXTRA_LATITUDE = "herdum.com.tracker2.extra.LATITUDE";
    private static final String EXTRA_LONGITUDE = "herdum.com.tracker2.extra.LONGITUDE";
    private static final String EXTRA_RESULTRECEIVER = "herdum.com.tracker2.extra.receiver" ;


    public GetAddressIntentService() {
        super("GetAddressIntentService");
    }

    public static void startActionFetchAddress(Context context, Double param1, Double param2,ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, GetAddressIntentService.class);
        intent.setAction(ACTION_ADDRESS);
        intent.putExtra(EXTRA_LATITUDE, param1);
        intent.putExtra(EXTRA_LONGITUDE, param2);
        intent.putExtra(EXTRA_RESULTRECEIVER,resultReceiver);

        context.startService(intent);
    }

    ResultReceiver resultReceiver;
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            resultReceiver = intent.getParcelableExtra(EXTRA_RESULTRECEIVER);

            if(resultReceiver==null){
                Log.i(TAG,getString(R.string.error_receiver_not_availale));
                return;
            }

            final String action = intent.getAction();
            if (ACTION_ADDRESS.equals(action)) {
                final Double param1 = intent.getDoubleExtra(EXTRA_LATITUDE,0.0f);
                final Double param2 = intent.getDoubleExtra(EXTRA_LONGITUDE,0.0f);
                handleActionFetchAddress(param1, param2);
            }
        }
    }


    private void handleActionFetchAddress(Double latitude, Double longitude) {
/*

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude,4);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.LOCATION_DATA_EXTRA,new ArrayList<Address>(addresses)); //jee:where to write this ADDRESS
            if(resultReceiver!=null){
                resultReceiver.send(Constants.SUCCESS_RESULT,bundle);
            }

        } catch (IOException e) {
            e.printStackTrace();
            resultReceiver.send(Constants.FAILURE_RESULT,null);

        }
*/      Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try{
            addresses = geocoder.getFromLocation(latitude, longitude,1);
            if(addresses==null || addresses.size()==0){
                Log.i(TAG,getString(R.string.error_address_unavailable));
                respondWithResilt(Constants.FAILURE_RESULT, getString(R.string.error_address_unavailable));
                return;
            }else {
                StringBuilder addressString = new StringBuilder();
                Address address = addresses.get(0);
                for(int i = 0;i<=address.getMaxAddressLineIndex();i++){
                    addressString.append(address.getAddressLine(i)+"\n");
                }
                respondWithResilt(Constants.SUCCESS_RESULT, addressString.toString());
            }
        }catch (IOException e){
            Log.i(TAG,getString(R.string.error_exception_while_getting_address));
            respondWithResilt(Constants.FAILURE_RESULT, getString(R.string.error_exception_while_getting_address));
        }

    }

    private void respondWithResilt(int resultCode, String resultMessage) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, resultMessage);
        resultReceiver.send(resultCode, bundle);
    }
}
