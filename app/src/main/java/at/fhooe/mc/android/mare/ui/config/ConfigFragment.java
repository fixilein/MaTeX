package at.fhooe.mc.android.mare.ui.config;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import at.fhooe.mc.android.mare.EditorActivity;
import at.fhooe.mc.android.mare.R;
import at.fhooe.mc.android.mare.document.Document;

public class ConfigFragment extends Fragment {

    private ConfigViewModel configViewModel;

    private Document mDocument;
    private Document.DocHeader mHeader;

    CheckBox cbToc, cbDate;
    private EditText edDate, edTitle, edSubTitle, edAuthor;
    private String dateBuffer;
    private TextView tvFontSize, tvMarginVert, tvMarginHor;
    private SeekBar seekBarFontSize, seekBarMarginVert, seekBarMarginHor;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        configViewModel = ViewModelProviders.of(this).get(ConfigViewModel.class);
        View root = inflater.inflate(R.layout.fragment_config, container, false);
        configViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        setHasOptionsMenu(false);

        mDocument = EditorActivity.mDocument;
        mHeader = mDocument.getHeader();

        cbToc = root.findViewById(R.id.fragment_config_checkBox_toc);
        cbToc.setChecked(mHeader.getToc());

        edTitle = root.findViewById(R.id.fragment_config_editText_title);
        edTitle.setText(mHeader.getTitle());

        edAuthor = root.findViewById(R.id.fragment_config_editText_author);
        edAuthor.setText(mHeader.getAuthor());

        edSubTitle = root.findViewById(R.id.fragment_config_editText_subtitle);
        edSubTitle.setText(mHeader.getSubtitle());

        dateBuffer = mHeader.getDate();
        if (dateBuffer.equals("\\today")) dateBuffer = "";

        edDate = root.findViewById(R.id.fragment_config_editText_date);
        edDate.setText(mHeader.getDate());

        cbDate = root.findViewById(R.id.fragment_config_checkBox_date_today);
        cbDate.setChecked(mHeader.getDate().equals("\\today"));
        edDate.setEnabled(!cbDate.isChecked());
        cbDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean _isChecked) {
                edDate.setEnabled(!_isChecked);
                if (_isChecked) {
                    dateBuffer = edDate.getText().toString();
                    edDate.setText(getContext().getString(R.string.LaTeX_today));
                } else {
                    edDate.setText(dateBuffer);
                }
            }
        });

        tvFontSize = root.findViewById(R.id.fragment_config_textView_font_size);


        seekBarFontSize = root.findViewById(R.id.fragment_config_seekBarFontSize);
        seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFontSize.setText(progress + "pt");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarFontSize.setProgress(mHeader.getFontSize());
        tvFontSize.setText(mHeader.getFontSize() + "pt");


        tvMarginVert = root.findViewById(R.id.fragment_config_textView_margin_vert);
        tvMarginHor = root.findViewById(R.id.fragment_config_textView_margin_hor);

        seekBarMarginVert = root.findViewById(R.id.fragment_config_seekbar_margin_vert);
        seekBarMarginHor = root.findViewById(R.id.fragment_config_seekbar_margin_hor);
        seekBarMarginVert.setProgress(mHeader.getMarginTopBot());
        seekBarMarginHor.setProgress(mHeader.getMarginLeftRight());

        seekBarMarginVert.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMarginVert.setText(String.format(getString(R.string.margin_mm), String.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarMarginHor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMarginHor.setText(String.format(getString(R.string.margin_mm), String.valueOf(progress)));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvMarginVert.setText(String.format(getString(R.string.margin_mm), String.valueOf(mHeader.getMarginTopBot())));
        tvMarginHor.setText(String.format(getString(R.string.margin_mm), String.valueOf(mHeader.getMarginLeftRight())));

        return root;
    }


    private void reloadHeader() {
        mHeader.setTitle(edTitle.getText().toString());
        mHeader.setSubtitle(edSubTitle.getText().toString());
        mHeader.setAuthor(edAuthor.getText().toString());
        mHeader.setDate(edDate.getText().toString());
        mHeader.setToc(cbToc.isChecked());
        mHeader.setFontSize(seekBarFontSize.getProgress());
        mHeader.setMarginLeftRight(seekBarMarginHor.getProgress());
        mHeader.setMarginTopBot(seekBarMarginVert.getProgress());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_config, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MaRe", "configFrag::onPause");
        save();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        int id = _item.getItemId();
        if (id == R.id.menu_fragment_item_save) {
            save();
            return true;
        }

        return super.onOptionsItemSelected(_item);
    }

    private void save() {
        reloadHeader();
        mDocument.saveFile(mHeader.toString(), mDocument.getContent());
    }


}