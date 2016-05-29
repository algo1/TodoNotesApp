package inplokesh.ac.iitkgp.cse.todonotes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewnoteActivity extends AppCompatActivity {

    String title, content;
    String docId;
    Bundle extras; // Extras Bundle differentiates b/w old and new notes
    DBHelper dbHelper;

    EditText noteEditText;
    Button footerActionButton;

    DialogFragment titleDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);

        getInfoFromLaunchingActivity();

        customizeActionBar();

        setContentView(R.layout.activity_newnote);

        prepareUI();

    }

    public void customizeActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!TextUtils.isEmpty(title)) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setNoteEditTextStatus(boolean enable) {
        noteEditText = (EditText) findViewById(R.id.note);
        noteEditText.setFocusable(enable);
        noteEditText.setClickable(enable);
    }


    public void getInfoFromLaunchingActivity() {
        extras = getIntent().getExtras();
        if (extras != null) {
            // Opening old noteEditText
            docId = extras.getString(AppConstants.getDocId());
            title = extras.getString(AppConstants.getTitle());
            content = extras.getString(AppConstants.getContent());
        } else {
            // Creating new noteEditText
            docId = Utils.getCurrentTimeStamp();
        }
    }


    public void prepareUI() {

        prepareEditor();


        prepareFooterButton();


    }


    public void prepareEditor() {
        noteEditText = (EditText) findViewById(R.id.note);
        noteEditText.setText(content);
        if (!TextUtils.isEmpty(content)) {
        }
    }


    public void updateUIPostDBUpdate() {
        if (noteEditText != null) {
            noteEditText.clearFocus();
        }
        if (footerActionButton != null) {
            footerActionButton.setText(AppConstants.getActionEdit());
        }

    }

    public void saveDocToDB() {
        if (extras != null) {
            // Update the note
            dbHelper.deleteNote(docId); // Todo check return value
            docId = Utils.getCurrentTimeStamp();
            dbHelper.insertNote(docId, title, noteEditText.getText().toString(), 1);
            Toast.makeText(TodoNotes.getContext(), AppConstants.getNoteUpdateSuccess(), Toast.LENGTH_LONG).show();
            updateUIPostDBUpdate();
        } else {
            // New note
            titleDialog = new MyAlertDialogFragment().newInstance(AppConstants.PURPOSE_TITLE_DIALOG);
            titleDialog.show(getSupportFragmentManager(), null);
        }
    }

    public void prepareFooterButton() {

        footerActionButton = (Button) findViewById(R.id.action);

        if (extras != null) {
            // Old note
//            setNoteEditTextStatus(false);
            footerActionButton.setText(AppConstants.getActionEdit());
        } else {
            // New note
            footerActionButton.setText(AppConstants.getActionDone());
        }

        footerActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (footerActionButton.getText().equals(AppConstants.getActionEdit())) {
                    // Start Editing and change the text of footer button
//                    setNoteEditTextStatus(true);
                    footerActionButton.setText(AppConstants.getActionDone());
                } else {
                    // Editing done , apply changes to DB
//                    setNoteEditTextStatus(false);
                    saveDocToDB();
                }
            }
        });
    }

    public void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setMessage("Are you sure you want to close without saving the current note ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        if (footerActionButton != null && footerActionButton.getText().equals(AppConstants.getActionDone())) {
            if (extras == null) {
                // In New Doc
                if (TextUtils.isEmpty(noteEditText.getText().toString())) {
                    // User not missing anything
                    super.onBackPressed();
                } else {
                    // Probably user misses something
                    showAlertDialog();
                }
            } else {
                // user edited existing doc
                showAlertDialog();
            }
        } else {
            super.onBackPressed();
        }


    }


    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int purpose) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("purpose", purpose);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int purpose = getArguments().getInt("purpose");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


            switch (purpose) {

                case AppConstants.PURPOSE_TITLE_DIALOG:

                    // Show Title Dialog
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View view = inflater.inflate(R.layout.title_dialog, null);
                    builder.setView(view);

                    final EditText newNoteTitleET = (EditText) view.findViewById(R.id.newNoteTitle);


                    builder.setTitle("Title")
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            // Todo enable this button only if the title is non empty
                                            ((NewnoteActivity) getActivity()).saveNewnoteToDB(newNoteTitleET.getText().toString());
                                        }
                                    }
                            )
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            ((NewnoteActivity) getActivity()).onUserCancelClick();
                                        }
                                    }
                            );
                    break;

                case AppConstants.PURPOSE_DELETE_DIALOG:
                    // Show Delete Dialog
                    builder.setTitle("Delete")
                            .setMessage("This action will delete the current note")
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            // Todo enable this button only if the title is non empty
                                            ((NewnoteActivity) getActivity()).deleteCurrentNote();
                                        }
                                    }
                            )
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    }
                            );
                    break;

            }


            return builder.create();
        }
    }

    public void saveNewnoteToDB(String title) {
        Log.d("MainActivity", "Inserting " + docId + "-" + title + "-" + noteEditText.getText().toString());
        boolean isInserted = dbHelper.insertNote(docId, title, noteEditText.getText().toString(), 1);
        Log.d("MainActivity", "Inserted value is " + String.valueOf(isInserted));
        Toast.makeText(TodoNotes.getContext(), AppConstants.getNoteInsertSuccess(), Toast.LENGTH_LONG).show();

        extras = new Bundle(); // Since the note needs to behave as old once it is saved in db
        updateUIPostDBUpdate();

    }

    public void deleteCurrentNote() {

        // Todo Should not be able to delete  unsaved new note

        Log.d("MainActivity", "Deleting " + docId + "-" + title + "-" + noteEditText.getText().toString());
        boolean isDeleted = dbHelper.updateNote(docId, title, noteEditText.getText().toString(), 0);
        if (isDeleted) {
            Toast.makeText(TodoNotes.getContext(), AppConstants.getMoveTrashSuccess(), Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(TodoNotes.getContext(), AppConstants.getErrorDeletingNote(), Toast.LENGTH_LONG).show();
        }
    }

    public void onUserCancelClick() {
        Toast.makeText(TodoNotes.getContext(), AppConstants.getTitleError(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deleteNote) {
            MyAlertDialogFragment alertDialogFragment = MyAlertDialogFragment.newInstance(AppConstants.getPurposeDeleteDialog());
            alertDialogFragment.show(getSupportFragmentManager(), "");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
