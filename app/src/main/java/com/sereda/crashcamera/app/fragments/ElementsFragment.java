package com.sereda.crashcamera.app.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import com.sereda.crashcamera.app.R;
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
        if (bundle != null) {
            id = bundle.getInt(DBHelper.ID);
        }

        db = DBHelper.getInstance(getActivity()).getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_elements, container, false);

        return view;
    }

    public void createAdapter(View view) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO + " WHERE " + DBHelper.PHOTO_ITEM_ID
                + " = ? ", new String[]{String.valueOf(id)});

        if (cursor.getCount() > 0) {
            String[] from = new String[]{DBHelper.PHOTO_URI};
            int[] to = new int[]{R.id.jazzy_iv_picture};

            JazzyGridView jazzyGridView = (JazzyGridView) view.findViewById(android.R.id.list);
            ElementsAdapter adapter = new ElementsAdapter(getActivity(), R.layout.item_list_jazzy_grid_view, cursor, from, to, 0);
            jazzyGridView.setAdapter(adapter);
            adapter.changeCursor(cursor);
            jazzyGridView.setLongClickable(true);
            jazzyGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
            jazzyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    MainActivity.loadImageFromDB = true;
//
//                    Fragment fragment = new UpdateLookupFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putString(DBHelper.ID, cursor.getString(cursor.getColumnIndex(DBHelper.ID)));
//                    bundle.putString(DBHelper.LOOKUP_NAME, cursor.getString(cursor.getColumnIndex(DBHelper.LOOKUP_NAME)));
//                    bundle.putString(DBHelper.LOOKUP_IMAGE_URI, cursor.getString(cursor.getColumnIndex(DBHelper.LOOKUP_IMAGE_URI)));
//                    bundle.putBoolean(Variables.IS_FROM_LIST_LOOKUP_FRAGMENT, true);
//                    fragment.setArguments(bundle);
//
//                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    transaction.addToBackStack(Variables.UPDATE_LOOKUP_FRAGMENT);
//                    if (fragment.isAdded()) {
//                        transaction.show(fragment);
//                        transaction.commit();
//                    } else {
//                        transaction.replace(R.id.container, fragment, Variables.UPDATE_LOOKUP_FRAGMENT);
//                        transaction.commit();
//                    }
                }
            });
            jazzyGridView.setMultiChoiceModeListener(new GridView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
//                    actionMode.getMenuInflater().inflate(R.menu.action_mode, menu);
//                    if (MainActivity.toolbar != null) {
//                        MainActivity.toolbar.setVisibility(View.GONE);
//                    }
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
//                    if (jazzyGridView != null) {
//                        final SparseBooleanArray sbArray = jazzyGridView.getCheckedItemPositions();
//                        switch (menuItem.getItemId()) {
//                            case R.id.action_mode_delete_button:
//                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//                                alertDialog.setTitle(getActivity().getResources().getString(R.string.dialog_window_lookup_delete_title));
//                                alertDialog.setMessage(getActivity().getResources().getString(R.string.dialog_window_lookup_delete_main_text));
//                                alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        for (int i = (sbArray.size() - 1); i >= 0; i--) {
//                                            int key = sbArray.keyAt(i);
//                                            if (sbArray.get(key)) {
//                                                cursorID = (Cursor) jazzyGridView.getItemAtPosition(key);
//                                                String id = cursorID.getString(cursor.getColumnIndex(DBHelper.ID));
//
//                                                deleteLookup(DBHelper.TABLE_LOOKUP, DBHelper.ID, id);
//                                                deleteLookup(DBHelper.TABLE_LOOKUP_HEAD, DBHelper.LOOKUP_HEAD_ID, id);
//                                                deleteLookup(DBHelper.TABLE_LOOKUP_BODY, DBHelper.LOOKUP_BODY_ID, id);
//                                                deleteLookup(DBHelper.TABLE_LOOKUP_LEGS, DBHelper.LOOKUP_LEGS_ID, id);
//                                                deleteLookup(DBHelper.TABLE_LOOKUP_BOOTS, DBHelper.LOOKUP_BOOTS_ID, id);
//                                            }
//                                        }
////                                            if (cursorID != null) {
////                                                cursorID.close();
//
//                                        createAdapter();
//
//                                        Toast.makeText(getActivity(),
//                                                getActivity().getResources().getString(R.string.dialog_window_lookup_delete_success),
//                                                Toast.LENGTH_SHORT).show();
//
//                                        actionMode.finish();
////                                            }
//                                    }
//                                });
//                                alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        actionMode.finish();
//                                    }
//                                });
//                                alertDialog.show();
//                                break;
//                            case R.id.action_mode_select_all_button:
//                                String[] columns = new String[]{DBHelper.ID, DBHelper.LOOKUP_NAME,
//                                        DBHelper.LOOKUP_IMAGE_URI};
//                                Cursor cursor = MainActivity.db.query(DBHelper.TABLE_LOOKUP, columns,
//                                        null, null, null, null, null);
//                                for (int i = 0; i < cursor.getCount(); i++) {
//                                    jazzyGridView.setItemChecked(i, true);
//                                }
//                                cursor.close();
//                                break;
//                            default:
//                                break;
//                        }
//                    }
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
//                    if (MainActivity.toolbar != null) {
//                        MainActivity.toolbar.setVisibility(View.VISIBLE);
//                    }
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
