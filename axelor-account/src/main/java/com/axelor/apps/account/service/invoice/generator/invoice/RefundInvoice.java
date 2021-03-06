/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.account.service.invoice.generator.invoice;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.exception.IExceptionMessage;
import com.axelor.apps.account.service.invoice.InvoiceToolService;
import com.axelor.apps.account.service.invoice.generator.InvoiceGenerator;
import com.axelor.apps.base.service.administration.GeneralServiceImpl;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.IException;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;

public class RefundInvoice extends InvoiceGenerator implements InvoiceStrategy {

	private static final Logger LOG = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );
	private Invoice invoice;

	
	public RefundInvoice(Invoice invoice) {
		
		super();

		this.invoice = invoice;
		
	}

	@Override
	public Invoice generate() throws AxelorException {
		
		LOG.debug("Créer un avoir pour la facture {}", new Object[] { invoice.getInvoiceId() });
		
		Invoice refund = Beans.get(InvoiceRepository.class).copy(invoice, true);
		
		refund.setOperationTypeSelect(this.inverseOperationType(refund.getOperationTypeSelect()));
		
		List<InvoiceLine> refundLines = new ArrayList<InvoiceLine>();
		if(refund.getInvoiceLineList() != null)  {
			refundLines.addAll( refund.getInvoiceLineList() );
		}
		
		populate( refund, refundLines );
		
		// Payment mode should not be the invoice payment mode. It must come
		// from the partner or the company, or be null.
		refund.setPaymentMode(InvoiceToolService.getPaymentMode(refund));

		if (refund.getPaymentMode() == null) {
			throw new AxelorException(String.format(I18n.get(IExceptionMessage.REFUND_INVOICE_1), GeneralServiceImpl.EXCEPTION), IException.MISSING_FIELD);
		}

		return refund;
		
	}
	
	@Override
	public void populate(Invoice invoice, List<InvoiceLine> invoiceLines) throws AxelorException {
		
		super.populate(invoice, invoiceLines);
	}
	
	
	/**
	 * Mets à jour les lignes de facture en appliquant la négation aux prix unitaires et
	 * au total hors taxe.
	 * 
	 * @param invoiceLines
	 */
	@Deprecated
	protected void refundInvoiceLines(List<InvoiceLine> invoiceLines){
		
		for (InvoiceLine invoiceLine : invoiceLines){
			
			invoiceLine.setQty(invoiceLine.getQty().negate());
			invoiceLine.setExTaxTotal(invoiceLine.getExTaxTotal().negate());
			
		}
		
	}

}
