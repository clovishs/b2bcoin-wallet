import {Component} from '@angular/core';

import { WalletServiceStore } from '../walletService.service';

@Component({
    selector: 'addresses',
    styleUrls: ['./addresses.scss'],
    templateUrl: './addresses.html'
})
export class Addresses {

    public options = {
        position: ["top", "right"],
        timeOut: 5000,
        lastOnBottom: false
    };

    constructor (private walletService: WalletServiceStore) {
    }

}
