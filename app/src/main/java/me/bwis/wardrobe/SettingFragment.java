package me.bwis.wardrobe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.UpdatePasswordCallback;

public class SettingFragment extends Fragment {


    private View.OnClickListener mLoginOnClickListener;
    private View.OnClickListener mFeedbackOnClickListener;
    private View.OnClickListener mHelpOnClickListener;

    private View mLoginView;

    private View rootView;



    public SettingFragment() {
        // Required empty public constructor
    }


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);


        mLoginView = rootView.findViewById(R.id.settings_login);

        initOnClickListeners();

        return rootView;
    }

    private void initOnClickListeners()
    {
        mLoginOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (WardrobeApplication.ApplicationState.IS_LOGGED_IN)
                {
                    showUserProfileDialog();
                }
                else
                {
                    showLoginDialog();
                }
                    

            }
        };
        mLoginView.setOnClickListener(mLoginOnClickListener);
    }

    private void showUserProfileDialog()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_profile, null);
        TextView mUsername = mView.findViewById(R.id.dialog_profile_username);
        TextView mEmail = mView.findViewById(R.id.dialog_profile_email);
        mUsername.setText(AVUser.getCurrentUser().getUsername());
        mEmail.setText(AVUser.getCurrentUser().getEmail());
        MaterialButton mChangePassword = mView.findViewById(R.id.dialog_profile_change_password);
        MaterialButton mLogout = mView.findViewById(R.id.dialog_profile_logout);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Change Password;
                showChangePasswordDialog();
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                AVUser.logOut();
                onLoggedOut();
            }
        });

    }

    private void showLoginDialog()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
        final EditText mEmail = mView.findViewById(R.id.dialog_login_email);
        final EditText mPassword = mView.findViewById(R.id.dialog_login_password);
        MaterialButton mLogin = mView.findViewById(R.id.dialog_login_button);
        MaterialButton mSignUp = mView.findViewById(R.id.dialog_sign_up_button);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean notEmpty = checkLoginInfo(mEmail.getText().toString(), mPassword.getText().toString());
                if (notEmpty)
                {
                    AVUser.logInInBackground(mEmail.getText().toString(), mPassword.getText().toString(), new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            if (e == null) {
                                dialog.dismiss();
                                onLoggedIn();
                            } else {
                                Toast.makeText(SettingFragment.this.getActivity(),R.string.toast_invalid_user, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showSignUpDialog();
            }
        });

    }

    private void onLoggedIn()
    {
        //fetch and render login user status
        Toast.makeText(getActivity(), "Success login with "+AVUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
        WardrobeApplication.ApplicationState.IS_LOGGED_IN = true;
        TextView login = rootView.findViewById(R.id.settings_login_text);
        login.setText("Hello, "+AVUser.getCurrentUser().getUsername()+"!");
    }

    private void onLoggedOut()
    {
        WardrobeApplication.ApplicationState.IS_LOGGED_IN = false;
        TextView login = rootView.findViewById(R.id.settings_login_text);
        login.setText(R.string.settings_login);
    }




    private boolean checkLoginInfo(String username, String password)
    {
        if (username.isEmpty() || password.isEmpty())
            return false;
        else return true;
    }


    private void showSignUpDialog()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_sign_up, null);
        final EditText mEmail = mView.findViewById(R.id.dialog_sign_up_email);
        final EditText mPassword = mView.findViewById(R.id.dialog_sign_up_password);
        final EditText mUsername = mView.findViewById(R.id.dialog_sign_up_username);
        MaterialButton mButton = mView.findViewById(R.id.dialog_sign_up_confirm_button);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AVUser user = new AVUser();// 新建 AVUser 对象实例
                user.setUsername(mUsername.getText().toString());// 设置用户名
                user.setPassword(mPassword.getText().toString());// 设置密码
                user.setEmail(mEmail.getText().toString());//设置邮箱
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                            dialog.dismiss();
                            AVUser.logOut();
                        } else {

                            Toast.makeText(SettingFragment.this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showChangePasswordDialog()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        final EditText mOldPassword = mView.findViewById(R.id.dialog_change_password_old);
        final EditText mNewPassword = mView.findViewById(R.id.dialog_change_password_new);
        MaterialButton mButton = mView.findViewById(R.id.dialog_change_password_confirm);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AVUser user = AVUser.getCurrentUser();
                user.updatePasswordInBackground(mOldPassword.getText().toString(), mNewPassword.getText().toString(),
                        new UpdatePasswordCallback() {
                            @Override
                            public void done(AVException e)
                            {
                                if (e == null) {
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(),0);
                                    dialog.dismiss();
                                } else {

                                    Toast.makeText(SettingFragment.this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

}
