package org.firstinspires.ftc.teamcode.pedro;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import androidx.annotation.Nullable;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.behaviors.BlockedBehavior;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.ivy.behaviors.EndCondition;
import com.pedropathing.ivy.behaviors.InterruptedBehavior;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class launchControl {

    DcMotorEx motor;
    DcMotorSimple feed = hardwareMap.get(DcMotorSimple.class, "feed");
    double rpm;
    double overspeedTolerance;
    boolean cycleStarted = false;
    ElapsedTime elapsedTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

    public Command launchCycle() {
        spinTo().execute();
        double motorRPM = motor.getVelocity()*(60.0/28.0);
        if (!cycleStarted) {
            elapsedTime.reset();
        }
        cycleStarted = true;

        if (elapsedTime.time() < 4) {
            if (Math.abs(rpm - motorRPM) > 200) {
                feed(false);
            } else {
                feed(true);
            }
        }



    }


    /**
     * Bang-bang controller, tries to spin the provided motor to provided rpm.
     * If motor rpm under rpm target, full positive power
     * If over rpm target + overspeedTolerance, full negative power
     */

    public Command spinTo() {
        double motorRPM = motor.getVelocity()*(60.0/28.0);
        if (rpm > motorRPM) {
            return Command.build()
                    .setExecute(() -> motor.setPower(1));
        }else if (rpm+overspeedTolerance < motorRPM){
            return Command.build()
                    .setExecute(() -> {
                        motor.setPower(0);

                    });
        }else {
            return Command.build()
                    .setExecute(() -> {
                        motor.setPower(rpm/4500);

                    });
        }

    }

    public void feed(boolean on) {
        feed.setPower(on? 1 : 0);
    }
}
