package draw.chemy.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import org.al.chemy.R;

import draw.chemy.creator.BasicShapesCreator;

public class BasicShapeUI extends ASettingsGroupUI {

    private BasicShapesCreator fCreator;
    private View fView;

    public BasicShapeUI(BasicShapesCreator aCreator) {
        fCreator = aCreator;
    }

    @Override
    public void fillView(LayoutInflater aInflater, ViewGroup aViewGroup, Context aContext) {
        fView = aInflater.inflate(R.layout.basic_shape_ui, aViewGroup);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.basic_shape_box:
                        fCreator.setShape(BasicShapesCreator.SHAPE.Box);
                        break;
                    case R.id.basic_shape_circle:
                        fCreator.setShape(BasicShapesCreator.SHAPE.Circle);
                        break;
                    case R.id.basic_shape_triangle:
                        fCreator.setShape(BasicShapesCreator.SHAPE.Triangle);
                        break;
                    case R.id.basic_shape_random:
                        fCreator.setShape(BasicShapesCreator.SHAPE.Random);
                        break;
                }
            }
        };

        RadioButton button = (RadioButton) fView.findViewById(R.id.basic_shape_box);
        button.setChecked(fCreator.getShape() == BasicShapesCreator.SHAPE.Box);
        button.setOnClickListener(listener);

        button = (RadioButton) fView.findViewById(R.id.basic_shape_circle);
        button.setChecked(fCreator.getShape() == BasicShapesCreator.SHAPE.Circle);
        button.setOnClickListener(listener);

        button = (RadioButton) fView.findViewById(R.id.basic_shape_triangle);
        button.setChecked(fCreator.getShape() == BasicShapesCreator.SHAPE.Triangle);
        button.setOnClickListener(listener);

        button = (RadioButton) fView.findViewById(R.id.basic_shape_random);
        button.setChecked(fCreator.getShape() == BasicShapesCreator.SHAPE.Random);
        button.setOnClickListener(listener);
    }
}

