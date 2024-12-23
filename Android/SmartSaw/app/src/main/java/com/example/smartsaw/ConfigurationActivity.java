package com.example.smartsaw;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ConfigurationActivity extends AppCompatActivity implements BTMessageBroadcastReceiver.BTMessageListener
{

  //#region Attributes

  private NumberField motorSpeed;
  private NumberField leftHorizontalLimit;
  private NumberField rightHorizontalLimit;
  private NumberField verticalLimit;
  private NumberField errorMargin;
  private ImageButton buttonBack;
  private ButtonWood buttonSaveChanges;
  private BTMessageBroadcastReceiver receiver;

  //#endregion

  //#region Activity Methods

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    initializeView();
    setListeners();
    setConnectionBluetoothService();
    setBroadcastConfiguration();
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
  }

  //#endregion

  //#region Broadcast Methods

  @Override
  public void onReceive(Intent intent)
  {
    String activity = intent.getStringExtra(BluetoothConnectionService.CONST_TOPIC);
    if (activity != null && activity.equals(ActivityType.CONFIGURATION_ACTIVITY.toString()))
    {
      String valor = intent.getStringExtra(BluetoothConnectionService.CONST_DATA);
      showToast("Se recibió " + valor);
    }
  }

  //#endregion

  //#region Private Methods

  private void initializeView()
  {
    setContentView(R.layout.activity_configuration);
    motorSpeed = findViewById(R.id.field_motor_speed);
    leftHorizontalLimit = findViewById(R.id.field_left_horizontal_limit);
    rightHorizontalLimit = findViewById(R.id.field_right_horizontal_limit);
    verticalLimit = findViewById(R.id.field_vertical_limit);
    errorMargin = findViewById(R.id.field_error_margin);
    buttonBack = findViewById(R.id.btn_configuration_back);
    buttonSaveChanges = findViewById(R.id.btn_save_changes);
    buttonSaveChanges.setButtonText(getString(R.string.save));
  }

  private void setListeners()
  {
    buttonBack.setOnClickListener(v ->
    {
      Intent intent = new Intent(ConfigurationActivity.this, OptionsActivity.class);
      startActivity(intent);
      finish();
    });

    buttonSaveChanges.setButtonOnClickListener(v ->
    {
      if (validateFields())
      {
        Toast.makeText(ConfigurationActivity.this, getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private boolean validateFields()
  {
    boolean result = true;
    result &= validateField(motorSpeed, getString(R.string.required_field));
    result &= validateField(leftHorizontalLimit, getString(R.string.required_field));
    result &= validateField(rightHorizontalLimit, getString(R.string.required_field));
    result &= validateField(verticalLimit, getString(R.string.required_field));
    result &= validateField(errorMargin, getString(R.string.required_field));
    return result;
  }

  private boolean validateField(NumberField field, String errorMessage)
  {
    if (field.getValue() == null || field.getValue() < 0)
    {
      field.setError(errorMessage);
      return false;
    }
    return true;
  }

  private void setConnectionBluetoothService()
  {
    BluetoothConnectionService connectionBtService = BluetoothConnectionServiceImpl.getInstance();
    connectionBtService.setActivity(this);
    connectionBtService.setContext(getApplicationContext());
  }

  private void setBroadcastConfiguration()
  {
    receiver = new BTMessageBroadcastReceiver(this);
    IntentFilter filter = new IntentFilter(BluetoothConnectionService.ACTION_DATA_RECEIVE);
    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
  }

  private void showToast(String message)
  {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }

  //#endregion
}
