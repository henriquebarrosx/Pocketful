<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lembrete de Pagamentos</title>
</head>

<div style="font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; width: 100% !important; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%;">
    <div style="width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff;">
        <div style="background-color: #3155D2; text-align: center; padding: 20px;">
            <h1 style="font-size: 28px; color: #FFFFFF; margin: 0;">Lembrete de vencimento</h1>
        </div>

        <div style="margin-top: 32px; padding: 0 20px;">
            <h1 style="font-size: 21px; margin-bottom: 8px;">Olá, ${account.name}!</h1>
            <span style="font-size: 16px;">Aqui estão os seus pagamentos a vencer:</span>
        </div>

        <div style="padding: 30px 20px;">
            <table cellspacing="0" cellpadding="0" style="width: 100%; border-collapse: collapse;">
                <#list payments as payment>
                    <#if payment?index != 0>
                        <tr style="font-size: 0; line-height: 0;"><td colspan="2" style="font-size: 0; line-height: 8px; background-color: transparent;">&nbsp;</td></tr> <!-- Espaçamento entre os itens -->
                    </#if>

                    <#if payment.overdue>
                        <tr>
                            <td style="padding: 10px; background-color: #FEECEC; margin-bottom: 8px;">
                                <div style="font-size: 16px; font-weight: 600; line-height: 1.4; color: #ef5350;">
                                    ${payment.description}<br>
                                    <span style="font-size: 14px; color: #ef5350;">${payment.deadlineAt}</span>
                                </div>
                            </td>
                            <td style="padding: 10px; background-color: #FEECEC; margin-bottom: 8px; text-align: right;">
                                <p style="font-size: 16px; font-weight: bold; color: #ef5350; margin: 0; padding-top: 8px;">${payment.amount}</p>
                            </td>
                        </tr>
                    <#else>
                        <tr>
                            <td style="padding: 10px; background-color: #F7F7F7; margin-bottom: 8px;">
                                <div style="font-size: 16px; font-weight: 600; line-height: 1.4; color: #6b6b6b;">
                                    ${payment.description}<br>
                                    <span style="font-size: 14px; color: #747474;">${payment.deadlineAt}</span>
                                </div>
                            </td>
                            <td style="padding: 10px; background-color: #F7F7F7; margin-bottom: 8px; text-align: right;">
                                <p style="font-size: 16px; font-weight: bold; color: #373737; margin: 0; padding-top: 8px;">${payment.amount}</p>
                            </td>
                        </tr>
                    </#if>
                </#list>
            </table>
        </div>

        <div style="text-align: center; padding: 10px 0 30px;">
            <p style="font-size: 16px; font-weight: 600; margin: 0; color: #3155D2;">Já realizou algum desses
                pagamentos?</p>
            <span style="font-size: 14px; color: #3155D2;">Atualize em nossa plataforma</span>
        </div>
    </div>

</div>

</html>
