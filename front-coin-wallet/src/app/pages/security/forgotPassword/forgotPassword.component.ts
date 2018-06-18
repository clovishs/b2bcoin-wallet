import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EmailValidator} from '../../../theme/validators';

import {TranslateService} from 'ng2-translate';
import {UserService} from '../../../services/com.b2beyond.api.user'

import {UserState} from '../../../user.state';

import {websiteId} from '../../../environment-config';

import 'style-loader!./forgotPassword.scss';

@Component({
    selector: 'forgotPassword',
    templateUrl: './forgotPassword.html',
})
export class ForgotPassword {

    public success: boolean = false;
    public messages: Array<string> = [];
    public errors: Array<string> = [];

    public form: FormGroup;
    public userId: AbstractControl;
    public submitted: boolean = false;

    constructor (private fb: FormBuilder,
                 private userState: UserState,
                 private UserService: UserService,
                 private router: Router,
                 private translate: TranslateService) {
        this.form = fb.group({
            'userId': ['', Validators.compose([Validators.required, EmailValidator.validate])]
        });

        this.userId = this.form.controls['userId'];

        // this language will be used as a fallback when a translation isn't found in the current language
        translate.setDefaultLang('en');
        // the lang to use, if the lang isn't available, it will use the current loader to get them
        var language = navigator.languages && navigator.languages[0].split("-")[0];
        console.log("Using language", language);
        translate.use(language);
    }

    public onSubmit (values: any): void {
        //console.log("Submitting password forgot email", values);
        this.messages = [];
        this.errors = [];

        values.websiteId = websiteId;

        this.submitted = true;
        if (this.form.valid) {
            this.UserService.forgotPassword(values).subscribe(result => {
                    this.success = true;
                    this.messages.push("Reset password mail was send");
                    this.submitted = false;
                },
                    error => {
                        this.errors.push("Something went wrong, try again later");
                        this.submitted = false;
                });
        }
    }
}