package com.openjfx.database.app.controller;

import com.fasterxml.jackson.databind.deser.impl.PropertyValue;
import com.openjfx.database.DDL;
import com.openjfx.database.DQL;
import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.component.DesignOptionBox;
import com.openjfx.database.app.controls.DesignTableView;
import com.openjfx.database.app.model.DesignTableModel;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.config.Constants.*;

/**
 * 设计表控制器
 *
 * @author yangkui
 * @since 1.0
 */
public class DesignTableController extends BaseController<JsonObject> {

    @FXML
    private TabPane tabPane;

    @FXML
    private HBox topBox;

    @FXML
    private DesignTableView<DesignTableModel> fieldTable;

    @FXML
    private SplitPane splitPane;

    private final List<Button> actionList = new ArrayList<>();

    @Override
    public void init() {
        intiTable(fieldTable, DesignTableModel.class);
        for (Tab tab : tabPane.getTabs()) {
            tab.setClosable(false);
        }
        var i = 0;
        for (Node child : topBox.getChildren()) {
            var button = (Button) child;
            if (i != 0) {
                actionList.add(button);
            }
            button.setOnAction(e -> listAction(button.getUserData().toString()));
            i++;
        }
        tabSelectChange(0);
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            tabSelectChange(index);
        });
        //listener fieldTable select change
        fieldTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var oIndex = oldValue.intValue();
            var index = newValue.intValue();
            //unbind last listener
            if (oIndex != -1) {
                var oItem = fieldTable.getItems().get(oIndex);
                oItem.charsetProperty().unbind();
            }
            if (index == -1) {
                return;
            }
            var item = fieldTable.getItems().get(index);

            item.charsetProperty().addListener((observable1, oldCharset, newCharset) -> {
                var box = new DesignOptionBox(null, DesignOptionBox.FieldDataType.STRING);
                var items = splitPane.getItems();
                if (items.size() > 1) {
                    items.remove(1);
                }
                items.add(box);
            });
        });
    }

    private <T> void intiTable(DesignTableView<T> tableView, Class<T> t) {
        var fields = t.getDeclaredFields();
        var tableColumns = tableView.getColumns();
        for (int i = 0; i < tableColumns.size(); i++) {
            var field = fields[i];
            var column = tableColumns.get(i);
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
        }
    }

    private void tabSelectChange(int index) {
        var cc = topBox.getChildren();
        var length = topBox.getChildren().size();
        cc.remove(1, length);
        for (Button node : actionList) {
            var useData = node.getUserData().toString();
            var indexNest = useData.split(",");
            for (var i : indexNest) {
                var ab = i.split("_");
                var a = Integer.parseInt(ab[0]);
                var b = Integer.parseInt(ab[1]);
                if (a == index) {
                    var size = cc.size();
                    if (b >= size - 1) {
                        cc.add(node);
                    } else {
                        cc.add(b, node);
                    }
                }
            }
        }
    }

    private void listAction(final String ij) {
        if ("0_1".equals(ij)) {
            var model = new DesignTableModel();
            var items = fieldTable.getItems();
            items.add(model);
            fieldTable.getSelectionModel().select(items.size() - 1);
        }
    }
}
