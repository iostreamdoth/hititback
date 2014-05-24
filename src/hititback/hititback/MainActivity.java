package hititback.hititback;

import java.io.Console;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import hititback.appbl.bl;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.facebook.*;
import com.facebook.model.*;
public class MainActivity extends Activity  implements
ConnectionCallbacks, OnConnectionFailedListener{
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;
	private boolean mIntentInProgress;
	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient mGoogleApiClient;
	private static final String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
		// Initializing google plus api client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this).addApi(Plus.API, null)
		.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		
		
		SignInButton btnGoogleSIgnIn = (SignInButton)findViewById(R.id.sign_in_button);
		btnGoogleSIgnIn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.sign_in_button)
				{
					signInWithGplus();
					    
				}

				
			}
			
		});
			
	}
	
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
		
	}
		
		protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
		mGoogleApiClient.disconnect();
		}
}
		
		



		public void onConnected(Bundle connectionHint) {
		  // We've resolved any connection errors.  mGoogleApiClient can be used to
		  // access Google APIs on behalf of the user.
			mSignInClicked = false;
		    Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		 
		    // Get user's information
		    getProfileInformation();
		 
		    // Update the UI after signin
		    updateUI(true);
		}
	
		/**
		 * Sign-in into google
		 * */
		private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
		mSignInClicked = true;
		resolveSignInError();
		}
	}
		
		/**
		 * Method to resolve any signin errors
		 * */
		private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
		try {
		mIntentInProgress = true;
		mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
		} 
		catch (SendIntentException e) {
		mIntentInProgress = false;
		
		mGoogleApiClient.connect();
		}
		}
		}


		
	 @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	      
	      if (requestCode == RC_SIGN_IN) {
	          if (resultCode != RESULT_OK) {
	              mSignInClicked = false;
	          }
	   
	          mIntentInProgress = false;
	   
	          if (!mGoogleApiClient.isConnecting()) {
	              mGoogleApiClient.connect();
	          }
	      }
	      
	      
	  }
	 
	 
	 @Override
	 public void onConnectionSuspended(int arg0) {
	     mGoogleApiClient.connect();
	     updateUI(false);
	 }
	 private void updateUI(boolean isSignedIn) {
		    if (isSignedIn) {
		        
		    } 
		    else {
		    }
		}
	 
	public void onbtnLogin(View v){
	    if(v.getId() == R.id.btnlogin){
	     
	    	EditText username = (EditText)findViewById(R.id.username);
	    	EditText password = (EditText)findViewById(R.id.password);
	    	String strUserName = username.getText().toString();
	    	String strPassword = password.getText().toString(); 
	    	strPassword = bl.md5(strPassword);
	    	TextView txtop = (TextView)findViewById(R.id.txtop);
	    	txtop.setText(strPassword);
	    }
	}
	
	public void onbtnFBLogin(View v){
		Log.v("Warning", "Starting FBLogin");
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			
		    // callback when session changes state
			@Override
		      public void call(Session session, SessionState state, Exception exception) {
		        if (session.isOpened()) {
		        	Log.v("Warning", "Starting call function");
		        	
		          // make request to the /me API
		          Request.newMeRequest(session, new Request.GraphUserCallback() {

		            // callback after Graph API response with user object
		            @Override
		            public void onCompleted(GraphUser user, Response response) {
		              if (user != null) {
		                TextView welcome = (TextView) findViewById(R.id.txtop);
		                welcome.setText("Hello " + user.getName() + "! With facebook");
		                Log.v("Warning", "User is not null");
		              }
		              else
		              {
		            	  Log.v("Warning", "User is null");
		              }
		            }
		          }).executeAsync();
		        }
		      }
		  });	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void getProfileInformation() {
	    try {
	        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
	            Person currentPerson = Plus.PeopleApi
	                    .getCurrentPerson(mGoogleApiClient);
	            String personName = currentPerson.getDisplayName();
	            String personPhotoUrl = currentPerson.getImage().getUrl();
	            String personGooglePlusProfile = currentPerson.getUrl();
	            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
	 
	            Log.e(TAG, "Name: " + personName + ", plusProfile: "
	                    + personGooglePlusProfile + ", email: " + email
	                    + ", Image: " + personPhotoUrl);
	            
	            TextView welcome = (TextView) findViewById(R.id.txtop);
                welcome.setText("Hello " + personName + "! with google plus");
	 
	            	 
	            // by default the profile url gives 50x50 px image only
	            // we can replace the value with whatever dimension we want by
	            // replacing sz=X
	            	 
	        } else {
	            Toast.makeText(getApplicationContext(),
	                    "Person information is null", Toast.LENGTH_LONG).show();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

}
