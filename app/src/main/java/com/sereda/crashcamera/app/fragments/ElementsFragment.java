package com.sereda.crashcamera.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.activities.PreviewActivity;
import com.sereda.crashcamera.app.adapters.ElementsAdapter;
import com.sereda.crashcamera.app.utils.DBHelper;
import com.twotoasters.jazzylistview.JazzyGridView;

public class ElementsFragment extends Fragment {
    private int id;
    private SQLiteDatabase db;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (null != bundle) {
            id = bundle.getInt(DBHelper.ID);
        }

        db = DBHelper.getInstance(getActivity()).getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_elements, container, false);

        Button buttonOpenPreview = (Button) view.findViewById(R.id.button_open_preview);
        buttonOpenPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PreviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(DBHelper.ID, id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        Button buttonComeBack = (Button) view.findViewById(R.id.button_come_back);
        buttonComeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public void createAdapter(View view) {
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO + " WHERE " + DBHelper.PHOTO_ITEM_ID
                + " = ? ", new String[]{String.valueOf(id)});

        if (cursor.getCount() > 0) {
            String[] from = new String[]{DBHelper.PHOTO_URI};
            int[] to = new int[]{R.id.jazzy_iv_picture};

            final JazzyGridView jazzyGridView = (JazzyGridView) view.findViewById(android.R.id.list);
            ElementsAdapter adapter = new ElementsAdapter(getActivity(), R.layout.item_list_jazzy_grid_view, cursor, from, to, 0);
            jazzyGridView.setAdapter(adapter);
            adapter.changeCursor(cursor);
            jazzyGridView.setLongClickable(true);
            jazzyGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
            jazzyGridView.setMultiChoiceModeListener(new GridView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    actionMode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
                    final SparseBooleanArray sbArray = jazzyGridView.getCheckedItemPositions();
                    switch (menuItem.getItemId()) {
                        case R.id.action_mode_delete_button:
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle(getActivity().getResources().getString(R.string.dialog_delete_title));
                            alertDialog.setMessage(getActivity().getResources().getString(R.string.dialog_delete_main_text));
                            alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i = (sbArray.size() - 1); i >= 0; i--) {
                                        int key = sbArray.keyAt(i);
                                        if (sbArray.get(key)) {
                                            Cursor cursorID = (Cursor) jazzyGridView.getItemAtPosition(key);
                                            String id = cursorID.getString(cursor.getColumnIndex(DBHelper.ID));

                                            Toast.makeText(getActivity(), " " + id, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    Toast.makeText(getActivity(),
                                            getActivity().getResources().getString(R.string.action_mode_deleted),
                                            Toast.LENGTH_SHORT).show();

                                    actionMode.finish();
                                }
                            });
                            alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    actionMode.finish();
                                }
                            });
                            alertDialog.show();
                            break;
                        case R.id.action_mode_select_all_button:
                            Cursor cursorSelectAll = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO, new String[]{});
                            for (int i = 0; i < cursorSelectAll.getCount(); i++) {
                                jazzyGridView.setItemChecked(i, true);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        createAdapter(view);
    }
}
