#!/bin/bash
scp -i /home/wagner/agendee.pem /home/wagner/workspace-wjaa/ranchucrutes-android/app/build/outputs/apk/app-debug.apk   ubuntu@agendee.com.br:/var/www/agendee.com.br/apk/version/agendee1.0.apk
