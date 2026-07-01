import {useState} from "react";
import * as React from "react";
import {sendMessage} from "@/services/api.ts";
import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

export function MessageSender() {

    const [apiKey, setApiKey] = useState('')
    const [partnerId, setPartnerId] = useState('partner-a')
    const [messageType, setMessageType] = useState('invoice')
    const [correlationId, setCorrelationId] = useState(() => crypto.randomUUID())
    const [xml, setXml] = useState('')
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
            <Label>
                API Schlüssel
                <Input type="password" value={apiKey} onChange={(e) => setApiKey(e.target.value)}/>
            </Label>
            <Label>
                Partner-ID
                <Select value={partnerId} onValueChange={setPartnerId}>
                    <SelectTrigger>
                        <SelectValue placeholder="Partner auswählen"/>
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="partner-a">Partner A</SelectItem>
                        <SelectItem value="partner-b">Partner B</SelectItem>
                    </SelectContent>
                </Select>
            </Label>
            <Label>
                Nachrichtentyp
                <Input type="text" value={messageType} onChange={(e) => setMessageType(e.target.value)}/>
            </Label>
            <Label>
                Correlation-ID
                <Input type="text" value={correlationId} readOnly/>
            </Label>
            <Label>
                XML Nachricht
                <Textarea value={xml} onChange={(e) => setXml(e.target.value)}/>
            </Label>
            <Button type="submit" disabled={loading}>
                Senden
            </Button>
        </form>
    )
}