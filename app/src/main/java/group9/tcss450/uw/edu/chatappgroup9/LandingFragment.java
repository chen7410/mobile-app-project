package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group9.tcss450.uw.edu.chatappgroup9.model.RecyclerViewAdapterLandingPageChat;
import group9.tcss450.uw.edu.chatappgroup9.utils.ListenManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LandingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LandingFragment extends Fragment implements RecyclerViewAdapterLandingPageChat.ChatItemListener {
    private int myChatCount = 0;
    private ListenManager chatsManager;
    private RecyclerViewAdapterLandingPageChat myAdapter;
    private OnFragmentInteractionListener mListener;
    private String mSendUrl2;
    private final String TAG = "LandingFragment";
    RecyclerView recyclerview;

    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("error", "onCreateView: we in this bitch ");
        JSONObject user = new JSONObject();
        View view = inflater.inflate(R.layout.fragment_landing, container, false);
        Log.e("NavigationActivity", "" + "LandingFragmentTag");
        // Inflate the layout for this fragment
        recyclerview = (RecyclerView) view.findViewById(R.id.Chats);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(new RecyclerViewAdapterLandingPageChat(new ArrayList<>()));
        myAdapter = (RecyclerViewAdapterLandingPageChat)recyclerview.getAdapter();
        myAdapter.setItemClickedListener(this);
        Button logout = view.findViewById(R.id.landingButtonLogout);
        logout.setOnClickListener(this::onLogoutPressed);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_shared_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_Chats))
                .appendQueryParameter("username", prefs.getString(getString(R.string.keys_shared_prefs_username), ""))
                .build();
//        mSendUrl2 = new Uri.Builder().scheme("https")
//                .appendPath(getString(R.string.ep_base_url))
//                .appendPath(getString(R.string.ep_get_chatInfo)).appendQueryParameter("chatId","1" )
//                .build()
//                .toString();


//        Log.e(TAG, "get chat id URL" + retrieve.toString());
        chatsManager = new ListenManager.Builder(retrieve.toString(),
                this::endOfSendMsgTask)
                .setExceptionHandler(this::handleError)
                .setDelay(1000)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        chatsManager.startListeningChats();
    }

    @Override
    public void onStop() {
        super.onStop();
         String latestMessage = chatsManager.stopListening();
        //  contentmanager.stopListening();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Save the most recent message timestamp
//        prefs.edit().putString(
//                getString(R.string.keys_prefs_time_stamp),
//                latestMessage)
//                .apply();
    }

    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }

    private void endOfSendMsgTask(JSONObject result) {
        Log.d(TAG, "endOfSendMsgTask: "+ result.toString());
        List<String> chatIdList = new ArrayList<>();
        try {
            JSONArray chatsJsonArray = result.getJSONArray("Chats");
            myChatCount = chatsJsonArray.length();
            for(int i = 0; i < chatsJsonArray.length() ; i++) {
                try{
                    JSONObject chatJson =  chatsJsonArray.getJSONObject(i);
                    String oneChatId = chatJson.get("chatid").toString();
                    chatIdList.add(oneChatId);
                    Log.d(TAG, "endOfSendMsgTask: "+ oneChatId.toString());
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        getActivity().runOnUiThread(() -> {
            for (String s : chatIdList) {
                if (myAdapter.getItemCount() != myChatCount) {
                    myAdapter.addData(s);
                    Log.e(TAG, "chat number count" + myAdapter.getItemCount());
                }
            }


        });

    }

    private void onLogoutPressed(View view) {
        if (mListener != null) {
            mListener.onLogout();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        getActivity().finishAndRemoveTask();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void chatItemOnClicked(String targetChatId) {

        Log.e(TAG, "targetChatId " + targetChatId);
        Fragment chatFrag = new ChatFragment();
        Bundle arg = new Bundle();
        arg.putString("TARGET_CHAT_ID", targetChatId);
        chatFrag.setArguments(arg);
        loadChatFragment(chatFrag, getString(R.string.keys_chat_fragment_tag));
    }

    private void loadChatFragment(Fragment frag, String theFragmentTag) {
        Log.e(TAG, "loadChatFragment");
        FragmentTransaction ft = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, theFragmentTag)
                .addToBackStack(null);
        ft.commit();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onLogout();
    }
}