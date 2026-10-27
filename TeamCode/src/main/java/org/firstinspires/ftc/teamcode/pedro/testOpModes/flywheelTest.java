package org.firstinspires.ftc.teamcode.pedro.testOpModes;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedro.launchControl;

@TeleOp(name = "Flywheel Test")
public class flywheelTest extends LinearOpMode {

    launchControl launchControl = new launchControl();
    DcMotorEx launcher;
    DcMotorEx launcher2;
    Command spinTo;
    Command spinTo2;
    @Override
    public void runOpMode() throws InterruptedException {
        launcher = hardwareMap.get(DcMotorEx.class, "testMotor");
        launcher2 =hardwareMap.get(DcMotorEx.class, "testMotor2");
        launcher.setDirection(DcMotorSimple.Direction.FORWARD);
        launcher2.setDirection(DcMotorSimple.Direction.REVERSE);
        double targetRPM = 3000;
        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.a){
                spinTo2 = launchControl.spinTo(targetRPM, launcher2, 200);

                spinTo2.execute();
            } else {
               // spinTo = launchControl.spinTo(0, launcher, 200);
                spinTo2 = launchControl.spinTo(0, launcher2, 200);
                spinTo2.execute();
                //spinTo.execute();
            }
            launcher.setPower(launcher2.getPower());
            if (gamepad1.dpad_down) {
                targetRPM -= 25;
            } else if (gamepad1.dpad_up) {
                targetRPM += 25;
            }
            telemetry.addData("Power (1)",launcher.getPower());
            telemetry.addData("Power (2)", launcher2.getPower());
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Current RPM (1)", launcher.getVelocity()*(60.0/28.0));
            telemetry.addData("Current RPM (2)", launcher2.getVelocity()*(60.0/28.0));
            updateTelemetry(telemetry);
        }


    }
}
