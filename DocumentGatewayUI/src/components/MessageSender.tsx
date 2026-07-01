import {useState} from "react";
import * as React from "react";
import {sendMessage} from "@/services/api.ts";
import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Button } from '@/components/ui/button'

export function MessageSender({ apiKey, partnerId }: Readonly<{ apiKey: string, partnerId: string }>) {

    const [messageType, setMessageType] = useState('invoice')
    const [correlationId, setCorrelationId] = useState(() => crypto.randomUUID())
    const [xml, setXml] = useState(`<message xmlns="http://documentgateway.de/invoice/v1">
  <id>INV-001</id>
</message>`)
    const [result, setResult] = useState<string | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault()
        setLoading(true)
        setResult(null)
        setError(null)

        try {
            const response = await sendMessage(apiKey, partnerId, messageType, correlationId, xml)
            setResult(response.correlationId)
            setCorrelationId(crypto.randomUUID())
        } catch (err) {
            setError('Fehler beim Senden')
        } finally {
            setLoading(false)
        }
    }

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
                <Label>
                    Nachrichtentyp

                </Label>
                <Input type="text" value={messageType} onChange={(e) => setMessageType(e.target.value)}/>
            </div>
            <div className="space-y-2">
                <Label>
                    Correlation-ID

                </Label>
                <Input type="text" value={correlationId} readOnly/>
            </div>
            <div className="space-y-2">
                <Label>
                    XML Nachricht

                </Label>
                <Textarea value={xml} onChange={(e) => setXml(e.target.value)}/>
            </div>
            {result && <p className="text-green-600">Gesendet: {result}</p>}
            {error && <p className="text-red-500">{error}</p>}
            <Button type="submit" disabled={loading}>
                Senden
            </Button>
        </form>
    )
}