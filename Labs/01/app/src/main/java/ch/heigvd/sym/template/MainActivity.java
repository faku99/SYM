/**
 * File     : MainActivity.java
 * Project  : TemplateActivity
 * Author   : Markus Jaton 2 juillet 2014
 * Fabien Dutoit 20 septembre 2016
 * IICT / HEIG-VD
 * <p>
 * mailto:fabien.dutoit@heig-vd.ch
 * <p>
 * This piece of code reads a [email_account / password ] combination.
 * It is used as a template project for the SYM module element given at HEIG-VD
 * Target audience : students IL, TS, IE [generally semester 1, third bachelor year]
 * <p>
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.heigvd.sym.template;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    // For logging purposes
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EMAIL_ENTERED = "EMAIL_ENTERED";
    public static final String PASSWORD_GIVEN = "PASSWORD_GIVEN";

    // Just for test purposes : please destroy !
    private static final String validEmail = "a@a.aaa";
    private static final String validPassword = "a";

    // GUI elements
    private EditText email = null;
    private EditText password = null;
    private Button signIn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the welcome screen / login authentication dialog
        setContentView(R.layout.authent);

        // Link to GUI elements
        this.email = (EditText) findViewById(R.id.email);
        this.password = (EditText) findViewById(R.id.password);
        this.signIn = (Button) findViewById(R.id.buttOk);

        // Then program action associated to "Ok" button
        signIn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                 * There you have to check out if the email/password
				 * combination given is valid or not
				 */
                String mail = email.getText().toString();
                String passwd = password.getText().toString();
                // Verify the validity of the email format
                if(!isEmailValid(email.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.incorrect_email_format), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isValid(mail, passwd)) {
					/* Ok, valid combination, do something or launch another activity...
					 * The current activity could be finished, but it is not mandatory.
					 * To launch activity MyActivity.class, try something like :
					 * 
					 * 			Intent intent = new Intent(this, ch.heigvd.sym.MyActivity.class);
					 * 			intent.putExtra("emailEntered", mail);
					 *			intent.putExtra("passwordGiven", passwd);
					 *			this.startActivity(intent); 
					 *
					 * Alternately, you could also startActivityForResult if you are awaiting a result.
					 * In the latter case, you have to indicate an int parameter to identify MyActivity
					 * 
					 * If you haven't anything more to do, you may finish()...
					 * But just display a small message before quitting...
					 */

                    Intent intent = new Intent(MainActivity.this, ch.heigvd.sym.template.PersonalInformation.class);
                    intent.putExtra(EMAIL_ENTERED, mail);
                    intent.putExtra(PASSWORD_GIVEN, passwd);
                    MainActivity.this.startActivity(intent);
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.good), Toast.LENGTH_LONG).show();
                    // Reset fields
                    email.setText("");
                    password.setText("");
                    //finish();
                } else {
                    // Wrong combination, display pop-up dialog and stay on login screen
                    showErrorDialog(mail, passwd);
                }
            }

        });
    }

    private boolean isValid(String mail, String passwd) {
        if (mail == null || passwd == null) {
            Log.w(TAG, "isValid(mail, passwd) - mail and passwd cannot be null !");
            return false;
        }
        // Return true if combination valid, false otherwise
        return (mail.equals(validEmail) && passwd.equals(validPassword));
    }

    protected void showErrorDialog(String mail, String passwd) {
		/*
		 * Pop-up dialog to show error
		 */
        AlertDialog.Builder alertbd = new AlertDialog.Builder(this);
        alertbd.setIcon(android.R.drawable.ic_dialog_alert);
        alertbd.setTitle(R.string.wronglogin);
        alertbd.setMessage(R.string.wrong);
        alertbd.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // we do nothing...
                // dialog close automatically
            }
        });
        alertbd.create().show();
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
