package org.firstinspires.ftc.teamcode.pedro;

import static com.pedropathing.ivy.commands.Commands.instant;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.pedro.Constants.feedPriorities;

import java.util.concurrent.TimeUnit;

public class subsystemControl {

    public DcMotorEx launchMotor;
    public DcMotorEx launchMotor2;
    public DcMotorSimple feed;
    public Servo launchDoor;
    public double targetRPM;
    double overspeedTolerance = 50;
    public boolean cycleStarted = false;
    public ElapsedTime elapsedTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
    public VoltageSensor batteryVoltageSensor;
    public double launchRPM = Constants.RobotConstants.launchRPM;
    OpMode runningOpMode;
    public double launchDoorClosed = 0.25;
    public double launchDoorOpen = 0.19;


    //The current feed request to be sent to the updateFeed() method to actually run feed
    subSysRequest currentFeedRequest = new subSysRequest();
    subSysRequest currentLaunchRequest = new subSysRequest();
    subSysRequest currentDoorRequest = new subSysRequest();



    /**
     * Class for feed request (constructor).
     * on: whether to run the feed or not.
     * priority: the priority of the feed request.
     */
    class subSysRequest {
        double value;
        int priority;
        int system;

    }

    /**
     * initializes all subsystems.
     * Must be called first to establish hardware map connections
     */
    public void initializeSubsystems(OpMode opMode) {
        runningOpMode = opMode;
        //launch motors
        launchMotor = opMode.hardwareMap.get(DcMotorEx.class, "testMotor2");
        launchMotor2 = opMode.hardwareMap.get(DcMotorEx.class, "testMotor");
        launchMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        launchMotor2.setDirection(DcMotorSimple.Direction.REVERSE);

        //feed motor
        feed = opMode.hardwareMap.get(DcMotorSimple.class, "feed");
        feed.setDirection(DcMotorSimple.Direction.REVERSE);

        launchDoor = opMode.hardwareMap.get(Servo.class, "launchDoor");

        //battery voltage sensor
        batteryVoltageSensor = opMode.hardwareMap.voltageSensor.iterator().next();

    }

    /**
     * updates feed,
     * ensures launchRPM is up to date,
     * updates telemetry,
     * executes scheduler,
     */
    public void updateAll() {
        updateFeed();
        updateLaunch();
        launchRPM = Constants.RobotConstants.launchRPM;
        runningOpMode.telemetry.update();
        Scheduler.execute();
    }

    /**
     *Runs the automatic launch cycle.
     * Must be called every loop cycle, since it does not run its own loop.
     * @return Command that runs the launch cycle
     */
    public Command launchCycle() {

        double motorRPM = launchMotor.getVelocity()*(60.0/28.0);

        double launchTime = 4- Math.pow((-(batteryVoltageSensor.getVoltage()-13)/2), 1.8);
        launchTime = 4;

        //checks if still within time limit (launchTime)
        //if yes, spin to 3000 rpm and submit feed request to feed if up to speed
        //if no, set rpm to 0, clear currentFeedRequest so that driver has control again
        /*if (elapsedTime.time(TimeUnit.SECONDS) < launchTime) {
            rpm = 3000;
            spinTo().execute();
            return Math.abs(rpm - motorRPM) < 100 ?
                    Command.build().setExecute(() -> submitFeedRequest(true, feedPriorities.launchCycle)) :
                    Command.build().setExecute(() -> submitFeedRequest(false, feedPriorities.launchCycle));

        } else {
            rpm = 0;

            return Command.build()
                    .setDone(()-> true)
                    .setEnd((endCondition) -> {
                        elapsedTime.reset();
                        clearFeed();
                    });
        }*/

        double finalLaunchTime = launchTime;
        return Command.build()

                .setStart(()->{
                    elapsedTime.reset();
                    submitSubSysRequest(0, feedPriorities.launchCycle, 1);
                })
                //Every cycle that it's running, spin to 3000 rpm and submit feed request to feed if up to speed
                .setExecute(() -> {
                    cycleStarted = true;
                    submitSubSysRequest(launchRPM, feedPriorities.launchCycle, 1);
                    if (Math.abs(targetRPM - motorRPM) < 100) {

                        launchDoor.setPosition(launchDoorOpen);
                        submitSubSysRequest(0.7, feedPriorities.launchCycle, 0);
                    } else {
                        launchDoor.setPosition(launchDoorClosed);
                        submitSubSysRequest(0, feedPriorities.launchCycle, 0);
                    }
                    //telemetry.addData("LaunchCycleRunning", true);


                })

                //Ends launch cycle when time is up
                .setDone(()-> elapsedTime.time(TimeUnit.SECONDS) > finalLaunchTime)

                //When ended, set rpm to 0, reset the timer, and clear currentFeedRequest so that driver has control again.
                .setEnd((endCondition) -> {
                    cycleStarted = false;
                    //targetRPM = 0;
                    elapsedTime.reset();
                    launchDoor.setPosition(launchDoorClosed);
                    submitSubSysRequest(0, feedPriorities.launchCycle, 1);
                    clearFeed();
                    clearLaunch();
                });
    }


    /**
     * Bang-bang controller, tries to spin the provided motor to provided rpm.
     * If motor rpm under rpm target, full positive power
     * If over rpm target + overspeedTolerance, full negative power
     * If within tolerance, medium power to keep it that speed
     */
    public Command spinTo = instant(()-> {
        double motorRPM = launchMotor.getVelocity()*(60.0/28.0);
        if (targetRPM +overspeedTolerance < motorRPM || targetRPM == 0){
            launchMotor.setPower(0);
        } else if (targetRPM > motorRPM+50 && motorRPM >= 0) {
            launchMotor.setPower(1);
        }else if ((targetRPM -motorRPM)<50){
            launchMotor.setPower(targetRPM /5500);
        }else {
            launchMotor.setPower(0);
        }
        launchMotor2.setPower(launchMotor.getPower());
    });

    //greater priority means higher priority
    /**
     * manages feed priority
     * @param request is the incoming request
     * If currentFeedRequest is null, set currentFeedRequest to request.
     * Checks priority of currentFeedRequest and request, whichever higher becomes currentFeedRequest.
     */
    public subSysRequest priorityManager(subSysRequest existingRequest, subSysRequest request) {

        if (existingRequest == null) {
            existingRequest = request;
        } else if (existingRequest.priority <= request.priority) {
            existingRequest = request;
        }
        return existingRequest;

    }
    /**
     * Submits a new feed request from scratch, and sends it through the feed priority manager
     * @param system identifies which subsystem the request is for, 0: feed, 1: launch motor, 2: launch door
     * @param value power to send to the feed motor (-1 to 1)
     * @param priority the priority of the request, higher number means higher priority, priorities found in Constants.feedPriorities
     */
    public void submitSubSysRequest(double value, int priority, int system) {
        subSysRequest request = new subSysRequest();
        request.value = value;
        request.priority = priority;
       // request.system = system;
       // subSysRequest workingRequest = null;
        switch (system) {
            case 0:
               // workingRequest = currentFeedRequest;
                currentFeedRequest = priorityManager(currentFeedRequest, request);
                break;
            case 1:
                currentLaunchRequest= priorityManager(currentLaunchRequest, request);
                break;
            case 2:
                currentDoorRequest = priorityManager(currentDoorRequest, request);
                break;

        }
       // priorityManager(workingRequest, request);

    }

    /**
     * Updates the feed based on the current feed request (currentFeedRequest)
     */
    public void updateFeed() {
        if (currentFeedRequest != null) {
            feed(currentFeedRequest.value);
        } else {
            feed(0);
        }
    }

    /**
     * Updates the targetRPM based on the current launch request (currentLaunchRequest)
     */
    public void updateLaunch() {
        if (currentLaunchRequest != null) {
            targetRPM = currentLaunchRequest.value;
        }
        spinTo.schedule();
    }
    /**
     * Clears the current feed request by setting currentFeedRequest to null
     */
    public void clearFeed(){
        currentFeedRequest = null;
    }

    /**
     * Clears the current launch request by setting currentLaunchRequest to null
     */
    public void clearLaunch() {currentLaunchRequest = null;}

    /**
     * Sets the power of the feed motor
     * @param power whether to run the feed or not
     */
    private void feed(double power) {
        feed.setPower(power);
    }
}
